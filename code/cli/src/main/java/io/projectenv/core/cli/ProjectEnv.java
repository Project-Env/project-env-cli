package io.projectenv.core.cli;

import com.google.gson.Gson;
import io.projectenv.core.cli.common.ServiceLoaderHelper;
import io.projectenv.core.cli.common.ToolSupportHelper;
import io.projectenv.core.cli.configuration.ProjectEnvConfiguration;
import io.projectenv.core.cli.configuration.toml.TomlConfigurationFactory;
import io.projectenv.core.cli.gson.BaseGsonBuilderFactory;
import io.projectenv.core.cli.installer.DefaultLocalToolInstallationManager;
import io.projectenv.core.process.ProcessOutputWriterAccessor;
import io.projectenv.core.toolsupport.api.ToolInfo;
import io.projectenv.core.toolsupport.spi.ImmutableToolSupportContext;
import io.projectenv.core.toolsupport.spi.ToolSupport;
import io.projectenv.core.toolsupport.spi.ToolSupportContext;
import io.projectenv.core.toolsupport.spi.ToolSupportException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;

@Command(name = "project-env")
public final class ProjectEnv implements Callable<Integer> {

    private static final Gson GSON = BaseGsonBuilderFactory
            .createBaseGsonBuilder()
            .create();

    @CommandLine.Option(names = {"--project-root"}, defaultValue = ".")
    protected File projectRoot;

    @CommandLine.Option(names = {"--config-file"}, required = true)
    protected File configFile;

    @Override
    public Integer call() {
        try {
            var configuration = readProjectEnvConfiguration();
            var toolInfos = installOrUpdateTools(configuration);
            writeOutput(toolInfos);

            return CommandLine.ExitCode.OK;
        } catch (Exception e) {
            var rootCauseMessage = ExceptionUtils.getRootCauseMessage(e);

            ProcessOutputWriterAccessor.getProcessInfoWriter().write("failed to install tools: {0}", rootCauseMessage);
            return CommandLine.ExitCode.SOFTWARE;
        }
    }

    public static void main(String[] args) {
        System.exit(executeProjectEnvCli(args));
    }

    public static int executeProjectEnvCli(String[] args) {
        return new CommandLine(new ProjectEnv()).execute(args);
    }

    private ProjectEnvConfiguration readProjectEnvConfiguration() throws IOException {
        return TomlConfigurationFactory.fromFile(configFile);
    }

    private Map<String, List<ToolInfo>> installOrUpdateTools(ProjectEnvConfiguration configuration) throws ProjectEnvException {
        var toolSupportContext = createToolSupportContext(configuration);

        return installOrUpdateTools(configuration, toolSupportContext);
    }

    private ToolSupportContext createToolSupportContext(ProjectEnvConfiguration configuration) {
        var toolsDirectory = new File(projectRoot, configuration.getToolsDirectory());
        var localToolInstallationManager = new DefaultLocalToolInstallationManager(toolsDirectory);

        return ImmutableToolSupportContext.builder()
                .projectRoot(projectRoot)
                .localToolInstallationManager(localToolInstallationManager)
                .build();
    }

    private Map<String, List<ToolInfo>> installOrUpdateTools(ProjectEnvConfiguration configuration, ToolSupportContext toolSupportContext) throws ProjectEnvException {
        try {
            var toolInstallationInfos = new HashMap<String, List<ToolInfo>>();
            for (ToolSupport<?> toolSupport : ServiceLoaderHelper.loadService(ToolSupport.class)) {
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
            ProcessOutputWriterAccessor.getProcessInfoWriter().write("installing {0}...", toolSupport.getToolIdentifier());

            toolInfos.add(toolSupport.prepareTool(toolConfiguration, toolSupportContext));
        }

        return toolInfos;
    }

    private void writeOutput(Map<String, List<ToolInfo>> toolInfos) {
        ProcessOutputWriterAccessor.getProcessResultWriter().write(GSON.toJson(toolInfos));
    }

}
