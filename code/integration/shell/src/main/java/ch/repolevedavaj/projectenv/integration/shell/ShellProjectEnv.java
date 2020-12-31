package ch.repolevedavaj.projectenv.integration.shell;

import ch.repolevedavaj.projectenv.core.ProjectToolDetails;
import ch.repolevedavaj.projectenv.core.configuration.ConfigurationFactory;
import ch.repolevedavaj.projectenv.core.configuration.GenericToolInstallationConfiguration;
import ch.repolevedavaj.projectenv.core.configuration.ProjectEnv;
import ch.repolevedavaj.projectenv.core.configuration.ProjectEnv.Tools.Installers;
import ch.repolevedavaj.projectenv.core.installer.ToolInstallerCollection;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShellProjectEnv {

    public static void main(String[] args) throws Exception {
        new ShellProjectEnv().setup(new File(args[0]));
    }

    public void setup(File projectEnvConfigurationFile) throws Exception {
        ProjectEnv projectEnvConfiguration = ConfigurationFactory.createFromFile(projectEnvConfigurationFile);

        File toolsDirectory = new File(projectEnvConfiguration.getTools().getDirectory());
        FileUtils.forceMkdir(toolsDirectory);

        Installers installers = projectEnvConfiguration.getTools().getInstallers();

        List<ProjectToolDetails> toolDetails = new ArrayList<>();
        if (installers.getJdk() != null) {
            toolDetails.add(ToolInstallerCollection.installTool(installers.getJdk(), toolsDirectory));
        }
        if (installers.getMaven() != null) {
            toolDetails.add(ToolInstallerCollection.installTool(installers.getMaven(), toolsDirectory));
        }
        if (installers.getNode() != null) {
            toolDetails.add(ToolInstallerCollection.installTool(installers.getNode(), toolsDirectory));
        }
        for (GenericToolInstallationConfiguration configuration : installers.getGeneric()) {
            toolDetails.add(ToolInstallerCollection.installTool(configuration, toolsDirectory));
        }

        writeShellProfile(projectEnvConfiguration, toolDetails);
    }

    private void writeShellProfile(ProjectEnv projectEnvConfiguration, List<ProjectToolDetails> toolDetailsList) throws IOException {
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

        FileUtils.write(new File(projectEnvConfiguration.getShellIntegration().getProfile()), sourceScriptBuilder, StandardCharsets.UTF_8);
    }

}
