package io.projectenv.core.cli;

import io.projectenv.core.cli.api.ToolInfo;
import io.projectenv.core.cli.api.ToolInfoParser;
import io.projectenv.core.cli.configuration.ProjectEnvConfiguration;
import io.projectenv.core.cli.configuration.toml.TomlConfigurationFactory;
import io.projectenv.core.cli.installer.DefaultLocalToolInstallationManager;
import io.projectenv.core.toolsupport.spi.ImmutableToolSupportContext;
import io.projectenv.core.toolsupport.spi.ToolSupport;
import io.projectenv.core.toolsupport.spi.ToolSupportContext;
import io.projectenv.core.toolsupport.spi.ToolSupportException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;

import static io.projectenv.core.commons.process.ProcessOutputWriterAccessor.getProcessInfoWriter;
import static io.projectenv.core.commons.process.ProcessOutputWriterAccessor.getProcessResultWriter;
import static picocli.CommandLine.ExitCode;

@Command(name = "project-env-cli")
public final class ProjectEnvCli implements Callable<Integer> {

    @Option(names = {"--project-root"}, defaultValue = ".")
    private File projectRoot;

    @Option(names = {"--config-file"}, required = true)
    private File configFile;

    @Option(names = {"--debug"})
    private boolean debug;

    @Override
    public Integer call() {
        try {
            var configuration = readProjectEnvConfiguration();
            var toolInfos = installOrUpdateTools(configuration);
            writeOutput(toolInfos);

            return ExitCode.OK;
        } catch (Exception e) {
            var rootCauseMessage = ExceptionUtils.getRootCauseMessage(e);

            getProcessInfoWriter().write("failed to install tools: {0}", rootCauseMessage);
            if (debug) {
                getProcessInfoWriter().write(e);
            }

            return ExitCode.SOFTWARE;
        }
    }

    public static void main(String[] args) {
        System.exit(executeProjectEnvCli(args));
    }

    public static int executeProjectEnvCli(String[] args) {
        return new CommandLine(new ProjectEnvCli()).execute(args);
    }

    private ProjectEnvConfiguration readProjectEnvConfiguration() throws IOException {
        return TomlConfigurationFactory.fromFile(configFile);
    }

    private Map<String, List<ToolInfo>> installOrUpdateTools(ProjectEnvConfiguration configuration) throws IOException {
        var toolSupportContext = createToolSupportContext(configuration);

        return installOrUpdateTools(configuration, toolSupportContext);
    }

    private ToolSupportContext createToolSupportContext(ProjectEnvConfiguration configuration) throws IOException {
        var toolsDirectory = new File(projectRoot, configuration.getToolsDirectory());
        if (!toolsDirectory.getCanonicalPath().startsWith(projectRoot.getCanonicalPath())) {
            throw new IllegalArgumentException("tools root must be located in project root");
        }

        var localToolInstallationManager = new DefaultLocalToolInstallationManager(toolsDirectory);

        return ImmutableToolSupportContext.builder()
                .projectRoot(projectRoot)
                .localToolInstallationManager(localToolInstallationManager)
                .build();
    }

    private Map<String, List<ToolInfo>> installOrUpdateTools(ProjectEnvConfiguration configuration, ToolSupportContext toolSupportContext) throws ProjectEnvException {
        try {
            var toolInstallationInfos = new LinkedHashMap<String, List<ToolInfo>>();
            for (ToolSupport<?> toolSupport : ServiceLoader.load(ToolSupport.class, ToolSupport.class.getClassLoader())) {
                List<ToolInfo> toolInfos = installOrUpdateTool(toolSupport, configuration, toolSupportContext);
                if (!toolInfos.isEmpty()) {
                    toolInstallationInfos.put(toolSupport.getToolIdentifier(), toolInfos);
                }
            }

            return toolInstallationInfos;
        } catch (ToolSupportException e) {
            throw new ProjectEnvException("failed to install tools", e);
        }
    }

    private <T> List<ToolInfo> installOrUpdateTool(ToolSupport<T> toolSupport, ProjectEnvConfiguration configuration, ToolSupportContext toolSupportContext) throws ToolSupportException {
        var toolSupportConfigurationClass = ToolSupportHelper.getToolSupportConfigurationClass(toolSupport);
        var toolConfigurations = configuration.getToolConfigurations(toolSupport.getToolIdentifier(), toolSupportConfigurationClass);
        if (toolConfigurations.isEmpty()) {
            return Collections.emptyList();
        }

        var toolInfos = new ArrayList<ToolInfo>();
        for (var toolConfiguration : toolConfigurations) {
            getProcessInfoWriter().write("installing {0}...", toolSupport.getToolIdentifier());

            toolInfos.add(toolSupport.prepareTool(toolConfiguration, toolSupportContext));
        }

        return toolInfos;
    }

    private void writeOutput(Map<String, List<ToolInfo>> toolInfos) {
        getProcessResultWriter().write(ToolInfoParser.toJson(toolInfos));
    }

}
