package io.projectenv.core.tools.installer.impl;

import io.projectenv.core.configuration.JdkConfiguration;
import io.projectenv.core.tools.info.JdkInfo;

public class JdkInstaller extends AbstractProjectToolInstaller<JdkConfiguration, JdkInfo> {

    @Override
    protected Class<JdkConfiguration> getToolConfigurationClass() {
        return JdkConfiguration.class;
    }

}
