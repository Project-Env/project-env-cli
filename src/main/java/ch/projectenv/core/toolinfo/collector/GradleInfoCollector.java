package ch.projectenv.core.toolinfo.collector;

import ch.projectenv.core.toolinfo.GradleInfo;
import ch.projectenv.core.toolinfo.ToolInfo;
import ch.projectenv.core.configuration.GradleConfiguration;
import ch.projectenv.core.toolinfo.ImmutableGradleInfo;

import java.util.List;

public class GradleInfoCollector extends AbstractToolInfoCollector<GradleConfiguration, GradleInfo> {

    @Override
    protected Class<GradleConfiguration> getToolConfigurationClass() {
        return GradleConfiguration.class;
    }

    @Override
    protected GradleInfo collectToolSpecificInfo(ToolInfo baseToolInfo, GradleConfiguration toolConfiguration) {
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
