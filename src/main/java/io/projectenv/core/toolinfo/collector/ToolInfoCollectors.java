package io.projectenv.core.toolinfo.collector;

import io.projectenv.core.common.ServiceLoaderHelper;
import io.projectenv.core.configuration.ToolConfiguration;
import io.projectenv.core.toolinfo.ToolInfo;

import java.util.ServiceLoader;

public class ToolInfoCollectors {

    private static final ServiceLoader<ToolInfoCollector> SERVICE_LOADER = ServiceLoaderHelper.loadService(ToolInfoCollector.class);

    public static <ToolConfigurationType extends ToolConfiguration, ToolInfoType extends ToolInfo> ToolInfoType collectToolInfo(ToolConfigurationType toolConfiguration, ToolInfoCollectorContext context) throws Exception {
        ToolInfoCollector<ToolConfigurationType, ToolInfoType> collector = getToolInfoCollectorForConfiguration(toolConfiguration);

        return collector.collectToolInfo(toolConfiguration, context);
    }

    @SuppressWarnings("unchecked")
    private static <ToolConfigurationType extends ToolConfiguration, ToolInfoType extends ToolInfo> ToolInfoCollector<ToolConfigurationType, ToolInfoType> getToolInfoCollectorForConfiguration(ToolConfigurationType toolConfiguration) {
        return (ToolInfoCollector<ToolConfigurationType, ToolInfoType>) SERVICE_LOADER
                .stream()
                .map(ServiceLoader.Provider::get)
                .filter(toolInfoCollector -> toolInfoCollector.supportsTool(toolConfiguration))
                .findFirst()
                .orElseThrow();
    }

}
