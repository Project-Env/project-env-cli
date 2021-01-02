package ch.repolevedavaj.projectenv.cli;

import ch.repolevedavaj.projectenv.core.ProjectToolDetails;
import ch.repolevedavaj.projectenv.core.configuration.ConfigurationFactory;
import ch.repolevedavaj.projectenv.core.configuration.ProjectEnv;
import ch.repolevedavaj.projectenv.core.installer.ToolInstallerCollection;
import org.apache.commons.io.FileUtils;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@Command(name = "project-env-cli")
public class ProjectEnvCli implements Callable<Integer> {

    @Option(names = {"--config-file"}, required = true)
    private File configFile;

    @Option(names = {"--output-mode"}, required = true)
    private CliOutputMode outputMode;

    @Option(names = {"--output-file"}, required = true)
    private File outputFile;

    public static void main(String[] args) throws Exception {
        int exitCode = new CommandLine(new ProjectEnvCli()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        ProjectEnv projectEnvConfiguration = ConfigurationFactory.createFromFile(configFile);

        File toolsDirectory = new File(projectEnvConfiguration.getTools().getDirectory());

        List<ProjectToolDetails> toolDetails = ToolInstallerCollection.installAllTools(projectEnvConfiguration, toolsDirectory);

        writeOutput(toolDetails);

        return 0;
    }

    private void writeOutput(List<ProjectToolDetails> toolDetails) throws IOException {
        switch (outputMode) {
            case INTERACTIVE_SHELL:
                writeInteractiveShellOutput(toolDetails);
                break;
            case GITHUB_ACTIONS:
                writeGithubActionsOutput(toolDetails);
                break;
        }
    }

    private void writeInteractiveShellOutput(List<ProjectToolDetails> toolDetailsList) throws IOException {
        StringBuilder sourceScriptBuilder = new StringBuilder("#!/bin/bash\n");

        for (ProjectToolDetails projectToolDetails : toolDetailsList) {
            for (Map.Entry<String, File> export : projectToolDetails.getExports().entrySet()) {
                sourceScriptBuilder.append("\nexport ").append(export.getKey()).append("=\"").append(export.getValue().getAbsolutePath()).append("\"");
            }

            for (File pathElement : projectToolDetails.getPathElements()) {
                sourceScriptBuilder.append("\nexport PATH=\"").append(pathElement.getAbsolutePath()).append(":$PATH\"");
            }
        }

        sourceScriptBuilder.append("\n");

        FileUtils.write(outputFile, sourceScriptBuilder, StandardCharsets.UTF_8);
    }

    private void writeGithubActionsOutput(List<ProjectToolDetails> toolDetailsList) throws IOException {
        StringBuilder sourceScriptBuilder = new StringBuilder("#!/bin/bash\n");

        for (ProjectToolDetails projectToolDetails : toolDetailsList) {
            for (Map.Entry<String, File> export : projectToolDetails.getExports().entrySet()) {
                sourceScriptBuilder.append("\necho \"").append(export.getKey()).append("=").append(export.getValue().getAbsolutePath()).append("\" >> $GITHUB_ENV");
            }

            for (File pathElement : projectToolDetails.getPathElements()) {
                sourceScriptBuilder.append("\necho \"").append(pathElement.getAbsolutePath()).append("\" >> $GITHUB_PATH");
            }
        }

        FileUtils.write(outputFile, sourceScriptBuilder, StandardCharsets.UTF_8);
    }

    private enum CliOutputMode {
        INTERACTIVE_SHELL, GITHUB_ACTIONS
    }

}
