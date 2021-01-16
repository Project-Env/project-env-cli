package ch.projectenv.core.installer;

import ch.projectenv.core.toolinfo.ToolInfo;
import ch.projectenv.core.configuration.ProjectEnvConfiguration;
import ch.projectenv.core.configuration.ToolConfiguration;
import ch.projectenv.core.configuration.ToolsConfiguration;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public final class ToolInstallers {

    @SuppressWarnings("rawtypes")
    private static final ServiceLoader<ProjectToolInstaller> SERVICE_LOADER = ServiceLoader.load(ProjectToolInstaller.class);

    public static List<ToolInfo> installAllTools(ProjectEnvConfiguration projectEnvConfiguration, File toolsDirectory) throws Exception {
        FileUtils.forceMkdir(toolsDirectory);

        ToolsConfiguration toolsConfiguration = projectEnvConfiguration.getToolsConfiguration();

        List<ToolInfo> toolDetails = new ArrayList<>();
        for (ToolConfiguration toolConfiguration : toolsConfiguration.getAllToolConfigurations()) {
            toolDetails.add(ToolInstallers.installTool(toolConfiguration, toolsDirectory));
        }

        return toolDetails;
    }

    public static ToolInfo installTool(ToolConfiguration configuration, File toolsDirectory) throws Exception {
        File toolDirectory = new File(toolsDirectory, configuration.getToolName());

        return getInstaller(configuration).installTool(configuration, toolDirectory);
    }

    @SuppressWarnings("unchecked")
    private static <ToolConfigurationType extends ToolConfiguration> ProjectToolInstaller<ToolConfigurationType, ?> getInstaller(ToolConfigurationType configuration) {
        return (ProjectToolInstaller<ToolConfigurationType, ?>) SERVICE_LOADER
                .stream()
                .map(ServiceLoader.Provider::get)
                .filter(projectToolInstaller -> projectToolInstaller.supportsTool(configuration))
                .findFirst()
                .orElseThrow();
    }

}
