package io.projectenv.core.tools.installer.impl;

import io.projectenv.core.configuration.GradleConfiguration;
import io.projectenv.core.tools.info.GradleInfo;

public class GradleInstaller extends AbstractProjectToolInstaller<GradleConfiguration, GradleInfo> {

    @Override
    protected Class<GradleConfiguration> getToolConfigurationClass() {
        return GradleConfiguration.class;
    }

}
