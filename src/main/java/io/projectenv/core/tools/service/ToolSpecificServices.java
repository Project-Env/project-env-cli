package io.projectenv.core.tools.service;

import io.projectenv.core.common.ServiceLoaderHelper;
import io.projectenv.core.configuration.ToolConfiguration;
import io.projectenv.core.tools.info.ToolInfo;
import io.projectenv.core.tools.service.collector.ToolInfoCollector;
import io.projectenv.core.tools.service.installer.ToolInstaller;
import io.projectenv.core.tools.service.installer.ToolInstallerException;
import io.projectenv.core.tools.service.resources.LocalToolResourcesProcessor;
import io.projectenv.core.tools.service.resources.LocalToolResourcesProcessorException;

import java.util.Optional;
import java.util.ServiceLoader;

@SuppressWarnings({"unchecked", "rawtypes"})
public final class ToolSpecificServices {

    private ToolSpecificServices() {
        // noop
    }

    public static void installTool(ToolConfiguration toolConfiguration, ToolSpecificServiceContext context) throws ToolInstallerException {
        Optional<ToolInstaller> toolInstaller = ToolSpecificServices.get(ToolInstaller.class, toolConfiguration);
        if (toolInstaller.isPresent()) {
            toolInstaller.get().installTool(toolConfiguration, context);
        }
    }

    public static void processLocalToolResources(ToolInfo toolInfo, ToolSpecificServiceContext context) throws LocalToolResourcesProcessorException {
        Optional<LocalToolResourcesProcessor> toolInstaller = ToolSpecificServices.get(LocalToolResourcesProcessor.class, toolInfo);
        if (toolInstaller.isPresent()) {
            toolInstaller.get().processLocalToolResources(toolInfo, context);
        }
    }

    public static <T extends ToolInfo> T collectToolInfo(ToolConfiguration toolConfiguration, ToolSpecificServiceContext context) {
        Optional<ToolInfoCollector> toolInstaller = ToolSpecificServices.get(ToolInfoCollector.class, toolConfiguration);
        if (toolInstaller.isPresent()) {
            return (T) toolInstaller.get().collectToolInfo(toolConfiguration, context);
        } else {
            throw new IllegalStateException("missing info collector for type " + toolConfiguration.getClass().getSimpleName());
        }
    }

    private static <T extends ToolSpecificService<S>, S> Optional<T> get(Class<T> serviceClass, S tool) {
        return ServiceLoaderHelper.loadService(serviceClass)
                .stream()
                .map(ServiceLoader.Provider::get)
                .filter(service -> service.supportsTool(tool))
                .findFirst();
    }

}
