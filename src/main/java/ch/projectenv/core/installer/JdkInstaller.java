package ch.projectenv.core.installer;

import ch.projectenv.core.configuration.JdkConfiguration;
import ch.projectenv.core.toolinfo.JdkInfo;

public class JdkInstaller extends AbstractProjectToolInstaller<JdkConfiguration, JdkInfo> {

    @Override
    protected Class<JdkConfiguration> getToolConfigurationClass() {
        return JdkConfiguration.class;
    }

}
