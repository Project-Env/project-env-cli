package io.projectenv.core.cli.command;

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
import io.projectenv.core.cli.parser.ToolInfoParser;
import io.projectenv.core.cli.service.ProjectEnvInstallService;
import io.projectenv.core.toolsupport.spi.ImmutableToolSupportContext;
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
 * allowing AI assistants to interact with ProjectEnv commands.
 */
@Command(name = "mcp", description = "Start MCP server to expose ProjectEnv tools")
public class ProjectEnvMcpCommand implements Callable<Integer> {

    private static final String SERVER_NAME = "project-env-mcp";
    private static final String INSTALL_TOOL_NAME = "project-env-install";
    private static final String ARG_PROJECT_ROOT = "projectRoot";
    private static final String ARG_CONFIG_FILE = "configFile";

    @Override
    public Integer call() {
        logServerStarting();

        final McpSyncServer server = createMcpServer();

        logServerStarted();

        return awaitServerShutdown(server);
    }

    private McpSyncServer createMcpServer() {
        final StdioServerTransportProvider transportProvider = new StdioServerTransportProvider(McpJsonMapper.getDefault());
        final SyncToolSpecification installToolSpec = createInstallToolSpecification();

        return McpServer.sync(transportProvider)
                .serverInfo(SERVER_NAME, VersionProvider.getVersion())
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .build())
                .tools(installToolSpec)
                .build();
    }

    private SyncToolSpecification createInstallToolSpecification() {
        return SyncToolSpecification.builder()
                .tool(createInstallTool())
                .callHandler(this::handleInstallToolCall)
                .build();
    }

    private Tool createInstallTool() {
        return Tool.builder()
                .name(INSTALL_TOOL_NAME)
                .description("Install or update tools configured in project-env.toml")
                .inputSchema(createInstallToolInputSchema())
                .build();
    }

    private JsonSchema createInstallToolInputSchema() {
        return new JsonSchema(
                "object",
                Map.of(
                        ARG_PROJECT_ROOT, Map.of(
                                "type", "string",
                                "description", "Path to project root directory"
                        ),
                        ARG_CONFIG_FILE, Map.of(
                                "type", "string",
                                "description", "Path to project-env.toml configuration file (normally in project root)"
                        )
                ),
                List.of(),
                false,
                Map.of(),
                Map.of()
        );
    }

    private CallToolResult handleInstallToolCall(Object exchange, CallToolRequest request) {
        try {
            final Map<String, Object> args = request.arguments();

            // Parse arguments
            final File projectRoot = parseProjectRoot(args);
            final File configFile = parseConfigFile(args, projectRoot);

            // Read configuration
            final ProjectEnvConfiguration configuration = TomlConfigurationFactory.fromFile(configFile);

            // Create tool support context
            final ToolSupportContext toolSupportContext = createToolSupportContext(projectRoot, configuration);

            // Execute installation service
            final var service = new ProjectEnvInstallService();
            final var toolInfos = service.installOrUpdateTools(configuration, toolSupportContext);

            // Convert to JSON and return as MCP result
            final String json = ToolInfoParser.toJson(toolInfos);
            return CallToolResult.builder()
                    .addContent(new TextContent(json))
                    .isError(false)
                    .build();
        } catch (Exception e) {
            return createErrorResult(e);
        }
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

