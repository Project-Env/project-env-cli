package io.projectenv.core.tools.collector;

import io.projectenv.core.common.ServiceLoaderHelper;
import io.projectenv.core.configuration.ToolConfiguration;
import io.projectenv.core.tools.info.ToolInfo;

import java.util.ServiceLoader;

public class ToolInfoCollectors {

    private ToolInfoCollectors() {
        // noop
    }

    @SuppressWarnings("rawtypes")
    private static final ServiceLoader<ToolInfoCollector> SERVICE_LOADER = ServiceLoaderHelper.loadService(ToolInfoCollector.class);

    public static <T extends ToolConfiguration, S extends ToolInfo> S collectToolInfo(T toolConfiguration, ToolInfoCollectorContext context) {
        ToolInfoCollector<T, S> collector = getToolInfoCollectorForConfiguration(toolConfiguration);

        return collector.collectToolInfo(toolConfiguration, context);
    }

    @SuppressWarnings("unchecked")
    private static <T extends ToolConfiguration, S extends ToolInfo> ToolInfoCollector<T, S> getToolInfoCollectorForConfiguration(T toolConfiguration) {
        return SERVICE_LOADER
                .stream()
                .map(ServiceLoader.Provider::get)
                .filter(toolInfoCollector -> toolInfoCollector.supportsTool(toolConfiguration))
                .findFirst()
                .orElseThrow();
    }

}
