package ch.repolevedavaj.projectenv.core.installer;

import ch.repolevedavaj.projectenv.core.ProjectToolDetails;
import ch.repolevedavaj.projectenv.core.configuration.GenericToolInstallationConfiguration;
import ch.repolevedavaj.projectenv.core.configuration.JdkInstallationConfiguration;
import ch.repolevedavaj.projectenv.core.configuration.MavenInstallationConfiguration;
import ch.repolevedavaj.projectenv.core.configuration.NodeInstallationConfiguration;

import java.io.File;
import java.util.Map;

public final class ToolInstallerCollection {

    private static final Map<Class<?>, ProjectToolInstaller<?>> TOOL_INSTALLERS = Map.of(
            JdkInstallationConfiguration.class, new JdkInstaller(),
            MavenInstallationConfiguration.class, new MavenInstaller(),
            NodeInstallationConfiguration.class, new NodeInstaller(),
            GenericToolInstallationConfiguration.class, new GenericToolInstaller()
    );

    public static <ToolInstallationConfiguration> ProjectToolDetails installTool(ToolInstallationConfiguration configuration, File toolsDirectory) throws Exception {
        return getInstaller(configuration).installTool(configuration, toolsDirectory);
    }

    private static <ToolInstallationConfiguration> ProjectToolInstaller<ToolInstallationConfiguration> getInstaller(ToolInstallationConfiguration configuration) {
        return (ProjectToolInstaller<ToolInstallationConfiguration>) TOOL_INSTALLERS.get(configuration.getClass());
    }

}
