package io.projectenv.core.toolinfo.collector;

import io.projectenv.core.toolinfo.GradleInfo;
import io.projectenv.core.toolinfo.ToolInfo;
import io.projectenv.core.configuration.GradleConfiguration;
import io.projectenv.core.toolinfo.ImmutableGradleInfo;

import java.util.List;

public class GradleInfoCollector extends AbstractToolInfoCollector<GradleConfiguration, GradleInfo> {

    @Override
    protected Class<GradleConfiguration> getToolConfigurationClass() {
        return GradleConfiguration.class;
    }

    @Override
    protected GradleInfo collectToolSpecificInfo(ToolInfo baseToolInfo, GradleConfiguration toolConfiguration, ToolInfoCollectorContext context) {
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
