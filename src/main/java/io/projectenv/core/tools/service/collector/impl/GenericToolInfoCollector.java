package io.projectenv.core.tools.service.collector.impl;

import io.projectenv.core.configuration.GenericToolConfiguration;
import io.projectenv.core.tools.info.GenericToolInfo;
import io.projectenv.core.tools.info.ImmutableGenericToolInfo;
import io.projectenv.core.tools.info.SimpleToolInfo;
import io.projectenv.core.tools.service.ToolSpecificServiceContext;

public class GenericToolInfoCollector extends AbstractSimpleToolInfoCollector<GenericToolConfiguration, GenericToolInfo> {

    @Override
    protected Class<GenericToolConfiguration> getToolConfigurationClass() {
        return GenericToolConfiguration.class;
    }

    @Override
    protected GenericToolInfo collectToolSpecificInfo(SimpleToolInfo baseToolInfo, GenericToolConfiguration toolConfiguration, ToolSpecificServiceContext context) {
        return ImmutableGenericToolInfo
                .builder()
                .from(baseToolInfo)
                .build();
    }

}
