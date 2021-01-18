package ch.projectenv.core.toolinfo.collector;

import ch.projectenv.core.toolinfo.GenericToolInfo;
import ch.projectenv.core.toolinfo.ToolInfo;
import ch.projectenv.core.configuration.GenericToolConfiguration;
import ch.projectenv.core.toolinfo.ImmutableGenericToolInfo;

public class GenericToolInfoCollector extends AbstractToolInfoCollector<GenericToolConfiguration, GenericToolInfo> {

    @Override
    protected Class<GenericToolConfiguration> getToolConfigurationClass() {
        return GenericToolConfiguration.class;
    }

    @Override
    protected GenericToolInfo collectToolSpecificInfo(ToolInfo baseToolInfo, GenericToolConfiguration toolConfiguration, ToolInfoCollectorContext context) {
        return ImmutableGenericToolInfo
                .builder()
                .from(baseToolInfo)
                .build();
    }

}
