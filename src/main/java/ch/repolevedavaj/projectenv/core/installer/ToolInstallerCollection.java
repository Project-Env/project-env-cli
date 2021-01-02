package ch.repolevedavaj.projectenv.core.installer;

import ch.repolevedavaj.projectenv.core.ProjectToolDetails;
import ch.repolevedavaj.projectenv.core.configuration.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ToolInstallerCollection {

    private static final Map<Class<?>, ProjectToolInstaller<?>> TOOL_INSTALLERS = Map.of(
            JdkInstallationConfiguration.class, new JdkInstaller(),
            MavenInstallationConfiguration.class, new MavenInstaller(),
            GradleInstallationConfiguration.class, new GradleInstaller(),
            NodeInstallationConfiguration.class, new NodeInstaller(),
            GenericToolInstallationConfiguration.class, new GenericToolInstaller()
    );

    public static List<ProjectToolDetails> installAllTools(ProjectEnv projectEnvConfiguration, File toolsDirectory) throws Exception {
        FileUtils.forceMkdir(toolsDirectory);

        ProjectEnv.Tools.Installers installers = projectEnvConfiguration.getTools().getInstallers();

        List<ProjectToolDetails> toolDetails = new ArrayList<>();
        if (installers.getJdk() != null) {
            toolDetails.add(ToolInstallerCollection.installTool(installers.getJdk(), toolsDirectory));
        }
        if (installers.getMaven() != null) {
            toolDetails.add(ToolInstallerCollection.installTool(installers.getMaven(), toolsDirectory));
        }
        if (installers.getGradle() != null) {
            toolDetails.add(ToolInstallerCollection.installTool(installers.getGradle(), toolsDirectory));
        }
        if (installers.getNode() != null) {
            toolDetails.add(ToolInstallerCollection.installTool(installers.getNode(), toolsDirectory));
        }
        for (GenericToolInstallationConfiguration configuration : installers.getGeneric()) {
            toolDetails.add(ToolInstallerCollection.installTool(configuration, toolsDirectory));
        }

        return toolDetails;
    }

    public static <ToolInstallationConfiguration> ProjectToolDetails installTool(ToolInstallationConfiguration configuration, File toolsDirectory) throws Exception {
        return getInstaller(configuration).installTool(configuration, toolsDirectory);
    }

    private static <ToolInstallationConfiguration> ProjectToolInstaller<ToolInstallationConfiguration> getInstaller(ToolInstallationConfiguration configuration) {
        return (ProjectToolInstaller<ToolInstallationConfiguration>) TOOL_INSTALLERS.get(configuration.getClass());
    }

}
