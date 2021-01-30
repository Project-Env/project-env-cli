package io.projectenv.core.installer;

import io.projectenv.core.common.ServiceLoaderHelper;
import io.projectenv.core.configuration.ProjectEnvConfiguration;
import io.projectenv.core.configuration.ToolConfiguration;
import io.projectenv.core.configuration.ToolsConfiguration;
import io.projectenv.core.toolinfo.ToolInfo;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public final class ToolInstallers {

    @SuppressWarnings("rawtypes")
    private static final ServiceLoader<ProjectToolInstaller> SERVICE_LOADER = ServiceLoaderHelper.loadService(ProjectToolInstaller.class);

    private ToolInstallers() {
        // noop
    }

    public static List<ToolInfo> installAllTools(ProjectEnvConfiguration projectEnvConfiguration, File projectRoot) throws ProjectToolInstallerException {
        try {
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
        } catch (IOException e) {
            throw new ProjectToolInstallerException("failed to install tools", e);
        }
    }

    private static ToolInfo installTool(ToolConfiguration configuration, ProjectToolInstallerContext context) throws ProjectToolInstallerException {
        return getInstaller(configuration).installTool(configuration, context);
    }

    @SuppressWarnings("unchecked")
    private static <T extends ToolConfiguration> ProjectToolInstaller<T, ?> getInstaller(T configuration) {
        return SERVICE_LOADER
                .stream()
                .map(ServiceLoader.Provider::get)
                .filter(projectToolInstaller -> projectToolInstaller.supportsTool(configuration))
                .findFirst()
                .orElseThrow();
    }

}
