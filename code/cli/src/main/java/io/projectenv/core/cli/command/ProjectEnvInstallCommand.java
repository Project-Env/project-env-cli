package io.projectenv.core.cli.command;

import io.projectenv.core.cli.configuration.ProjectEnvConfiguration;
import io.projectenv.core.cli.parser.ToolInfoParser;
import io.projectenv.core.cli.service.ProjectEnvInstallService;
import io.projectenv.core.cli.shell.TemplateProcessor;
import io.projectenv.core.commons.process.ProcessOutput;
import io.projectenv.core.toolsupport.spi.ToolInfo;
import io.projectenv.core.toolsupport.spi.ToolSupportContext;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Command(name = "install")
public class ProjectEnvInstallCommand extends AbstractProjectEnvCliCommand {

    @Option(names = {"--output-template"})
    private String outputTemplate;

    @Option(names = {"--output-file"})
    private File outputFile;

    @Override
    protected void callInternal(ProjectEnvConfiguration configuration, ToolSupportContext toolSupportContext) throws IOException {
        var service = new ProjectEnvInstallService();
        var toolInfos = service.installOrUpdateTools(configuration, toolSupportContext);
        writeOutput(toolInfos);
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
            ProcessOutput.writeInfoMessage("Failed to make file {0} executable", target.getCanonicalPath());
        }
    }

    private void writeOutputToStdOutput(String content) {
        ProcessOutput.writeResult(content);
    }

}
