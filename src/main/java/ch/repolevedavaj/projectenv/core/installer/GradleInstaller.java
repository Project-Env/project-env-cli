package ch.repolevedavaj.projectenv.core.installer;

import ch.repolevedavaj.projectenv.core.configuration.GradleConfiguration;
import ch.repolevedavaj.projectenv.core.toolinfo.GradleInfo;

public class GradleInstaller extends AbstractProjectToolInstaller<GradleConfiguration, GradleInfo> {

    @Override
    protected Class<GradleConfiguration> getToolConfigurationClass() {
        return GradleConfiguration.class;
    }

}
