package io.projectenv.core.cli.command;

import io.projectenv.core.cli.ProjectEnvException;
import io.projectenv.core.cli.ToolSupportHelper;
import io.projectenv.core.cli.configuration.ProjectEnvConfiguration;
import io.projectenv.core.cli.parser.ToolInfoParser;
import io.projectenv.core.cli.shell.TemplateProcessor;
import io.projectenv.core.commons.process.ProcessOutput;
import io.projectenv.core.toolsupport.spi.ToolInfo;
import io.projectenv.core.toolsupport.spi.ToolSupport;
import io.projectenv.core.toolsupport.spi.ToolSupportContext;
import io.projectenv.core.toolsupport.spi.ToolSupportException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Command(name = "install")
public class ProjectEnvInstallCommand extends AbstractProjectEnvCliCommand {

    @Option(names = {"--output-template"})
    private String outputTemplate;

    @Option(names = {"--output-file"})
    private File outputFile;

    @Override
    protected void callInternal(ProjectEnvConfiguration configuration, ToolSupportContext toolSupportContext) throws IOException {
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
            if (toolSupport.isAvailable(toolConfiguration)) {
                ProcessOutput.writeInfoMessage("installing {0}...", toolSupport.getToolIdentifier());
                toolInfos.add(toolSupport.prepareTool(toolConfiguration, toolSupportContext));
            } else {
                ProcessOutput.writeInfoMessage("{0} is not available, skipping installation", toolSupport.getToolIdentifier());
            }
        }

        return toolInfos;
    }

    private void writeOutput(Map<String, List<ToolInfo>> toolInfos) throws IOException {
        String content = prepareOutput(toolInfos);
        if (outputFile != null) {
            writeOutputToFile(content, outputFile);
        } else {
            writeOutputToStdOutput(content);
        }
    }

    private String prepareOutput(Map<String, List<ToolInfo>> toolInfos) throws IOException {
        if (outputTemplate == null) {
            return ToolInfoParser.toJson(toolInfos);
        } else {
            return TemplateProcessor.processTemplate(outputTemplate, toolInfos);
        }
    }

    private void writeOutputToFile(String content, File target) throws IOException {
        FileUtils.write(target, content, StandardCharsets.UTF_8);

        if (!SystemUtils.IS_OS_WINDOWS && !target.setExecutable(true)) {
            ProcessOutput.writeInfoMessage("failed to make file {0} executable", target.getCanonicalPath());
        }
    }

    private void writeOutputToStdOutput(String content) {
        ProcessOutput.writeResult(content);
    }

}
