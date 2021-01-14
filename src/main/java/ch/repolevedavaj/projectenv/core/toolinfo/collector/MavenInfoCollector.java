package ch.repolevedavaj.projectenv.core.toolinfo.collector;

import ch.repolevedavaj.projectenv.core.configuration.MavenConfiguration;
import ch.repolevedavaj.projectenv.core.toolinfo.ImmutableMavenInfo;
import ch.repolevedavaj.projectenv.core.toolinfo.MavenInfo;
import ch.repolevedavaj.projectenv.core.toolinfo.ToolInfo;

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
