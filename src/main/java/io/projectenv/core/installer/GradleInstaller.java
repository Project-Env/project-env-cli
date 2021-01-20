package io.projectenv.core.installer;

import io.projectenv.core.configuration.GradleConfiguration;
import io.projectenv.core.toolinfo.GradleInfo;

public class GradleInstaller extends AbstractProjectToolInstaller<GradleConfiguration, GradleInfo> {

    @Override
    protected Class<GradleConfiguration> getToolConfigurationClass() {
        return GradleConfiguration.class;
    }

}
