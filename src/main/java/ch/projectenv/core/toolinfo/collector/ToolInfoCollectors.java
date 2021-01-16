package ch.projectenv.core.toolinfo.collector;

import ch.projectenv.core.toolinfo.ToolInfo;
import ch.projectenv.core.configuration.ToolConfiguration;

import java.io.File;
import java.util.ServiceLoader;

public class ToolInfoCollectors {

    @SuppressWarnings("rawtypes")
    private static final ServiceLoader<ToolInfoCollector> SERVICE_LOADER = ServiceLoader.load(ToolInfoCollector.class);

    public static <ToolConfigurationType extends ToolConfiguration, ToolInfoType extends ToolInfo> ToolInfoType collectToolInfo(ToolConfigurationType toolConfiguration, File toolBinariesDirectory) throws Exception {
        ToolInfoCollector<ToolConfigurationType, ToolInfoType> collector = getToolInfoCollectorForConfiguration(toolConfiguration);

        return collector.collectToolInfo(toolConfiguration, toolBinariesDirectory);
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
