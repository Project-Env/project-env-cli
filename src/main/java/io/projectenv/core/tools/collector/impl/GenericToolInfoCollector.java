package io.projectenv.core.tools.collector.impl;

import io.projectenv.core.configuration.GenericToolConfiguration;
import io.projectenv.core.tools.collector.ToolInfoCollectorContext;
import io.projectenv.core.tools.info.GenericToolInfo;
import io.projectenv.core.tools.info.ImmutableGenericToolInfo;
import io.projectenv.core.tools.info.ToolInfo;

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
