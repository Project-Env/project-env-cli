package ch.projectenv.core.installer;

import ch.projectenv.core.configuration.GradleConfiguration;
import ch.projectenv.core.toolinfo.GradleInfo;

public class GradleInstaller extends AbstractProjectToolInstaller<GradleConfiguration, GradleInfo> {

    @Override
    protected Class<GradleConfiguration> getToolConfigurationClass() {
        return GradleConfiguration.class;
    }

}
