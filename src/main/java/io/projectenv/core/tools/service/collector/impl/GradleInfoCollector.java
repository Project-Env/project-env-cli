package io.projectenv.core.tools.service.collector.impl;

import io.projectenv.core.configuration.GradleConfiguration;
import io.projectenv.core.tools.info.GradleInfo;
import io.projectenv.core.tools.info.ImmutableGradleInfo;
import io.projectenv.core.tools.info.SimpleToolInfo;
import io.projectenv.core.tools.service.ToolSpecificServiceContext;

import java.util.List;

public class GradleInfoCollector extends AbstractSimpleToolInfoCollector<GradleConfiguration, GradleInfo> {

    @Override
    protected Class<GradleConfiguration> getToolConfigurationClass() {
        return GradleConfiguration.class;
    }

    @Override
    protected GradleInfo collectToolSpecificInfo(SimpleToolInfo baseToolInfo, GradleConfiguration toolConfiguration, ToolSpecificServiceContext context) {
        return ImmutableGradleInfo
                .builder()
                .from(baseToolInfo)
                .build();
    }

    @Override
    protected List<String> getAdditionalPathElements() {
        return List.of("/bin");
    }

    @Override
    protected String getPrimaryExecutableName() {
        return "gradle";
    }

}
