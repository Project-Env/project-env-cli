package io.projectenv.core.installer;

import io.projectenv.core.common.ServiceLoaderHelper;
import io.projectenv.core.configuration.ProjectEnvConfiguration;
import io.projectenv.core.configuration.ToolConfiguration;
import io.projectenv.core.configuration.ToolsConfiguration;
import io.projectenv.core.toolinfo.ToolInfo;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public final class ToolInstallers {

    private static final ServiceLoader<ProjectToolInstaller> SERVICE_LOADER = ServiceLoaderHelper.loadService(ProjectToolInstaller.class);

    public static List<ToolInfo> installAllTools(ProjectEnvConfiguration projectEnvConfiguration, File projectRoot) throws Exception {
        ToolsConfiguration toolsConfiguration = projectEnvConfiguration.getToolsConfiguration();

        File toolsDirectory = new File(projectRoot, toolsConfiguration.getToolsDirectory());
        FileUtils.forceMkdir(toolsDirectory);

        List<ToolInfo> toolDetails = new ArrayList<>();
        for (ToolConfiguration toolConfiguration : toolsConfiguration.getAllToolConfigurations()) {
            ProjectToolInstallerContext context = ImmutableProjectToolInstallerContext
                    .builder()
                    .projectRoot(projectRoot)
                    .toolRoot(new File(toolsDirectory, toolConfiguration.getToolName()))
                    .build();

            toolDetails.add(installTool(toolConfiguration, context));
        }

        return toolDetails;
    }

    private static ToolInfo installTool(ToolConfiguration configuration, ProjectToolInstallerContext context) throws Exception {
        return getInstaller(configuration).installTool(configuration, context);
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
