package ch.projectenv.core.toolinfo.collector;

import ch.projectenv.core.toolinfo.MavenInfo;
import ch.projectenv.core.toolinfo.ToolInfo;
import ch.projectenv.core.configuration.MavenConfiguration;
import ch.projectenv.core.toolinfo.ImmutableMavenInfo;

import java.io.File;
import java.util.List;

public class MavenInfoCollector extends AbstractToolInfoCollector<MavenConfiguration, MavenInfo> {

    @Override
    protected MavenInfo collectToolSpecificInfo(ToolInfo baseToolInfo, MavenConfiguration toolConfiguration) {
        return ImmutableMavenInfo
                .builder()
                .from(baseToolInfo)
                .globalSettingsFile(toolConfiguration
                        .getGlobalSettingsFile()
                        .map(File::new))
                .userSettingsFile(toolConfiguration
                        .getUserSettingsFile()
                        .map(File::new))
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
