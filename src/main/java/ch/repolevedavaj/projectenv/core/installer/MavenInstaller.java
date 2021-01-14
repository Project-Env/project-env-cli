package ch.repolevedavaj.projectenv.core.installer;

import ch.repolevedavaj.projectenv.core.configuration.MavenConfiguration;
import ch.repolevedavaj.projectenv.core.toolinfo.MavenInfo;

public class MavenInstaller extends AbstractProjectToolInstaller<MavenConfiguration, MavenInfo> {

    @Override
    protected Class<MavenConfiguration> getToolConfigurationClass() {
        return MavenConfiguration.class;
    }

}
