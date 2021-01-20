package io.projectenv.core.installer;

import io.projectenv.core.configuration.JdkConfiguration;
import io.projectenv.core.toolinfo.JdkInfo;

public class JdkInstaller extends AbstractProjectToolInstaller<JdkConfiguration, JdkInfo> {

    @Override
    protected Class<JdkConfiguration> getToolConfigurationClass() {
        return JdkConfiguration.class;
    }

}
