package io.projectenv.core.cli.command;

import io.projectenv.core.cli.ProjectEnvException;
import io.projectenv.core.cli.ToolSupportHelper;
import io.projectenv.core.cli.configuration.ProjectEnvConfiguration;
import io.projectenv.core.cli.parser.ToolInfoParser;
import io.projectenv.core.commons.process.ProcessOutput;
import io.projectenv.core.toolsupport.spi.ToolInfo;
import io.projectenv.core.toolsupport.spi.ToolSupport;
import io.projectenv.core.toolsupport.spi.ToolSupportContext;
import io.projectenv.core.toolsupport.spi.ToolSupportException;
import picocli.CommandLine.Command;

import java.util.*;

@Command(name = "install")
public class ProjectEnvInstallCommand extends AbstractProjectEnvCliCommand {

    @Override
    protected void callInternal(ProjectEnvConfiguration configuration, ToolSupportContext toolSupportContext) {
        writeOutput(installOrUpdateTools(configuration, toolSupportContext));
    }

    private Map<String, List<ToolInfo>> installOrUpdateTools(ProjectEnvConfiguration configuration, ToolSupportContext toolSupportContext) {
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
            ProcessOutput.writeInfoMessage("installing {0}...", toolSupport.getToolIdentifier());

            toolInfos.add(toolSupport.prepareTool(toolConfiguration, toolSupportContext));
        }

        return toolInfos;
    }

    private void writeOutput(Map<String, List<ToolInfo>> toolInfos) {
        ProcessOutput.writeResult(ToolInfoParser.toJson(toolInfos));
    }

}
