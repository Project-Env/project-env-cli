package io.projectenv.core.cli.command;

import com.google.gson.Gson;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema.*;
import io.projectenv.core.cli.VersionProvider;
import io.projectenv.core.cli.configuration.ProjectEnvConfiguration;
import io.projectenv.core.cli.configuration.toml.TomlConfigurationFactory;
import io.projectenv.core.cli.http.DefaultHttpClientProvider;
import io.projectenv.core.cli.index.DefaultToolsIndexManager;
import io.projectenv.core.cli.installer.DefaultLocalToolInstallationManager;
import io.projectenv.core.cli.service.ProjectEnvInstallService;
import io.projectenv.core.toolsupport.spi.ImmutableToolSupportContext;
import io.projectenv.core.toolsupport.spi.ToolInfo;
import io.projectenv.core.toolsupport.spi.ToolSupportContext;
import org.apache.commons.lang3.Strings;
import picocli.CommandLine.Command;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * MCP (Model Context Protocol) server command that exposes ProjectEnv tools via stdio.
 * <p>
 * This command starts a JSON-RPC server that implements the MCP protocol,
 * allowing AI assistants to query tool-specific metadata.
 */
@Command(name = "mcp", description = "Start MCP server to expose ProjectEnv tools")
public class ProjectEnvMcpCommand implements Callable<Integer> {

    private static final String SERVER_NAME = "project-env-mcp";
    private static final String TOOL_INFO_NAME = "project-env-tool-info";

    // Argument names
    private static final String ARG_PROJECT_ROOT = "projectRoot";
    private static final String ARG_CONFIG_FILE = "configFile";
    private static final String ARG_TOOL_NAME = "toolName";

    // Supported tool identifiers
    private static final String TOOL_MAVEN = "maven";
    private static final String TOOL_JDK = "jdk";

    @Override
    public Integer call() {
        logServerStarting();

        final McpSyncServer server = createMcpServer();

        logServerStarted();

        return awaitServerShutdown(server);
    }

    private McpSyncServer createMcpServer() {
        final StdioServerTransportProvider transportProvider = new StdioServerTransportProvider(McpJsonMapper.getDefault());
        final SyncToolSpecification toolInfoSpec = createToolInfoSpecification();

        return McpServer.sync(transportProvider)
                .serverInfo(SERVER_NAME, VersionProvider.getVersion())
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .build())
                .tools(toolInfoSpec)
                .build();
    }

    private SyncToolSpecification createToolInfoSpecification() {
        return SyncToolSpecification.builder()
                .tool(createToolInfoTool())
                .callHandler(this::handleToolInfoCall)
                .build();
    }

    private Tool createToolInfoTool() {
        return Tool.builder()
                .name(TOOL_INFO_NAME)
                .description("Get tool-specific metadata (version, settings files, etc.) for Maven or JDK")
                .inputSchema(createToolInfoInputSchema())
                .build();
    }

    private JsonSchema createToolInfoInputSchema() {
        return new JsonSchema(
                "object",
                Map.of(
                        ARG_TOOL_NAME, Map.of(
                                "type", "string",
                                "description", "Tool name (maven or jdk)",
                                "enum", List.of(TOOL_MAVEN, TOOL_JDK)
                        ),
                        ARG_PROJECT_ROOT, Map.of(
                                "type", "string",
                                "description", "Path to project root directory"
                        ),
                        ARG_CONFIG_FILE, Map.of(
                                "type", "string",
                                "description", "Path to project-env.toml configuration file (normally in project root)"
                        )
                ),
                List.of(ARG_TOOL_NAME),
                false,
                Map.of(),
                Map.of()
        );
    }

    /**
     * Handles tool info requests by returning tool-specific metadata for the requested tool.
     * <p>
     * Internally calls the installation service to ensure tools are installed,
     * then extracts and returns only the tool-specific metadata for the requested tool.
     *
     * @param exchange the MCP exchange context
     * @param request the tool call request
     * @return CallToolResult containing tool-specific metadata as JSON
     */
    private CallToolResult handleToolInfoCall(Object exchange, CallToolRequest request) {
        try {
            final Map<String, Object> args = request.arguments();

            // Parse arguments
            final String toolName = parseToolName(args);
            final File projectRoot = parseProjectRoot(args);
            final File configFile = parseConfigFile(args, projectRoot);

            // Read configuration
            final ProjectEnvConfiguration configuration = TomlConfigurationFactory.fromFile(configFile);

            // Create tool support context
            final ToolSupportContext toolSupportContext = createToolSupportContext(projectRoot, configuration);

            // Execute installation service to get tool info
            final var service = new ProjectEnvInstallService();
            final var allToolInfos = service.installOrUpdateTools(configuration, toolSupportContext);

            // Extract only the requested tool's metadata
            final Map<String, Object> toolSpecificMetadata = extractToolMetadata(allToolInfos, toolName);

            // Convert to JSON and return as MCP result
            final Gson gson = new Gson();
            final String json = gson.toJson(toolSpecificMetadata);

            return CallToolResult.builder()
                    .addContent(new TextContent(json))
                    .isError(false)
                    .build();
        } catch (Exception e) {
            return createErrorResult(e);
        }
    }

    /**
     * Parses and validates the tool name from request arguments.
     */
    private String parseToolName(Map<String, Object> args) {
        if (!args.containsKey(ARG_TOOL_NAME)) {
            throw new IllegalArgumentException("Missing required argument: " + ARG_TOOL_NAME);
        }

        final String toolName = args.get(ARG_TOOL_NAME).toString().toLowerCase();

        if (!TOOL_MAVEN.equals(toolName) && !TOOL_JDK.equals(toolName)) {
            throw new IllegalArgumentException(
                "Invalid tool name: " + toolName + ". Supported tools: " + TOOL_MAVEN + ", " + TOOL_JDK
            );
        }

        return toolName;
    }

    /**
     * Extracts tool-specific metadata from the installation results.
     * <p>
     * Returns only the toolSpecificMetadata map for the requested tool,
     * or an error if the tool is not found or has no metadata.
     *
     * @param allToolInfos all tool installation results
     * @param toolName the requested tool name
     * @return map containing only tool-specific metadata
     */
    private Map<String, Object> extractToolMetadata(Map<String, List<ToolInfo>> allToolInfos, String toolName) {
        if (!allToolInfos.containsKey(toolName)) {
            throw new IllegalStateException("Tool '" + toolName + "' is not configured in project-env.toml");
        }

        final List<ToolInfo> toolInfoList = allToolInfos.get(toolName);

        if (toolInfoList.isEmpty()) {
            throw new IllegalStateException("No installation info found for tool: " + toolName);
        }

        // Get the first (and typically only) ToolInfo for this tool
        final ToolInfo toolInfo = toolInfoList.getFirst();
        final Map<String, Object> metadata = toolInfo.getToolSpecificMetadata();

        if (metadata == null || metadata.isEmpty()) {
            throw new IllegalStateException("Tool '" + toolName + "' has no specific metadata available");
        }

        return metadata;
    }

    private File parseProjectRoot(Map<String, Object> args) {
        final String projectRootPath = args.containsKey(ARG_PROJECT_ROOT)
                ? args.get(ARG_PROJECT_ROOT).toString()
                : ".";
        return new File(projectRootPath);
    }

    private File parseConfigFile(Map<String, Object> args, File projectRoot) {
        final String configFilePath = args.containsKey(ARG_CONFIG_FILE)
                ? args.get(ARG_CONFIG_FILE).toString()
                : "project-env.toml";

        if (Strings.CS.equals(configFilePath, "project-env.toml")) {
            return new File(projectRoot, "project-env.toml");
        } else {
            return new File(configFilePath);
        }
    }

    private ToolSupportContext createToolSupportContext(File projectRoot, ProjectEnvConfiguration configuration) throws IOException {
        final File toolsDirectory = new File(projectRoot, configuration.getToolsDirectory());
        if (!toolsDirectory.getCanonicalPath().startsWith(projectRoot.getCanonicalPath())) {
            throw new IllegalArgumentException("Tools root must be located in project root");
        }

        final var localToolInstallationManager = new DefaultLocalToolInstallationManager(toolsDirectory);
        final var httpClientProvider = new DefaultHttpClientProvider();
        final var toolsIndexManager = new DefaultToolsIndexManager(toolsDirectory, httpClientProvider);

        return ImmutableToolSupportContext.builder()
                .projectRoot(projectRoot)
                .localToolInstallationManager(localToolInstallationManager)
                .toolsIndexManager(toolsIndexManager)
                .httpClientProvider(httpClientProvider)
                .build();
    }

    private CallToolResult createErrorResult(Exception e) {
        return CallToolResult.builder()
                .addContent(new TextContent("Tool execution failed: " + e.getMessage()))
                .isError(true)
                .build();
    }

    private int awaitServerShutdown(McpSyncServer server) {
        final CountDownLatch shutdownLatch = new CountDownLatch(1);
        registerShutdownHook(server, shutdownLatch);

        try {
            shutdownLatch.await();
            return 0;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logServerInterrupted();
            return 1;
        }
    }

    private void registerShutdownHook(McpSyncServer server, CountDownLatch shutdownLatch) {
        final Thread shutdownHook = new Thread(() -> {
            logServerShuttingDown();
            server.close();
            shutdownLatch.countDown();
        }, "project-env-mcp-shutdown");

        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    private void logServerStarting() {
        System.err.println("ProjectEnv MCP Server starting...");
    }

    private void logServerStarted() {
        System.err.println("ProjectEnv MCP Server started successfully");
    }

    private void logServerShuttingDown() {
        System.err.println("ProjectEnv MCP Server shutting down...");
    }

    private void logServerInterrupted() {
        System.err.println("Server interrupted");
    }


}

