package io.projectenv.core.toolinfo.collector;

import io.projectenv.core.toolinfo.GenericToolInfo;
import io.projectenv.core.toolinfo.ToolInfo;
import io.projectenv.core.configuration.GenericToolConfiguration;
import io.projectenv.core.toolinfo.ImmutableGenericToolInfo;

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
