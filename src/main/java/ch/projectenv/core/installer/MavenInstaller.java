package ch.projectenv.core.installer;

import ch.projectenv.core.configuration.MavenConfiguration;
import ch.projectenv.core.toolinfo.MavenInfo;

public class MavenInstaller extends AbstractProjectToolInstaller<MavenConfiguration, MavenInfo> {

    @Override
    protected Class<MavenConfiguration> getToolConfigurationClass() {
        return MavenConfiguration.class;
    }

}
