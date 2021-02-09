package io.projectenv.core.tools.collector.impl;

import io.projectenv.core.configuration.MavenConfiguration;
import io.projectenv.core.tools.collector.ToolInfoCollectorContext;
import io.projectenv.core.tools.info.ImmutableMavenInfo;
import io.projectenv.core.tools.info.MavenInfo;
import io.projectenv.core.tools.info.ToolInfo;

import java.io.File;
import java.util.List;

public class MavenInfoCollector extends AbstractToolInfoCollector<MavenConfiguration, MavenInfo> {

    @Override
    protected MavenInfo collectToolSpecificInfo(ToolInfo baseToolInfo, MavenConfiguration toolConfiguration, ToolInfoCollectorContext context) {
        return ImmutableMavenInfo
                .builder()
                .from(baseToolInfo)
                .globalSettingsFile(toolConfiguration
                        .getGlobalSettingsFile()
                        .map(value -> new File(context.getProjectRoot(), value)))
                .userSettingsFile(toolConfiguration
                        .getUserSettingsFile()
                        .map(value -> new File(context.getProjectRoot(), value)))
                .build();
    }

    @Override
    protected Class<MavenConfiguration> getToolConfigurationClass() {
        return MavenConfiguration.class;
    }

    @Override
    protected List<String> getAdditionalPathElements() {
        return List.of("/bin");
    }

    @Override
    protected String getPrimaryExecutableName() {
        return "mvn";
    }

}
