package ch.repolevedavaj.projectenv.core.installer;

import ch.repolevedavaj.projectenv.core.configuration.JdkConfiguration;
import ch.repolevedavaj.projectenv.core.toolinfo.JdkInfo;

public class JdkInstaller extends AbstractProjectToolInstaller<JdkConfiguration, JdkInfo> {

    @Override
    protected Class<JdkConfiguration> getToolConfigurationClass() {
        return JdkConfiguration.class;
    }

}
