package io.projectenv.core.tools.installer;

import io.projectenv.core.common.ServiceLoaderHelper;
import io.projectenv.core.configuration.ToolConfiguration;

import java.util.ServiceLoader;

public final class ToolInstallers {

    @SuppressWarnings("rawtypes")
    private static final ServiceLoader<ProjectToolInstaller> SERVICE_LOADER = ServiceLoaderHelper.loadService(ProjectToolInstaller.class);

    private ToolInstallers() {
        // noop
    }

    public static void installTool(ToolConfiguration configuration, ProjectToolInstallerContext context) throws ProjectToolInstallerException {
        getInstaller(configuration).installTool(configuration, context);
    }

    @SuppressWarnings("unchecked")
    private static <T extends ToolConfiguration> ProjectToolInstaller<T> getInstaller(T configuration) {
        return SERVICE_LOADER
                .stream()
                .map(ServiceLoader.Provider::get)
                .filter(projectToolInstaller -> projectToolInstaller.supportsTool(configuration))
                .findFirst()
                .orElseThrow();
    }

}
