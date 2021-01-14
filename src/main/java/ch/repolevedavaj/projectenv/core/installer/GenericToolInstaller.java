package ch.repolevedavaj.projectenv.core.installer;

import ch.repolevedavaj.projectenv.core.configuration.GenericToolConfiguration;
import ch.repolevedavaj.projectenv.core.toolinfo.GenericToolInfo;

public class GenericToolInstaller extends AbstractProjectToolInstaller<GenericToolConfiguration, GenericToolInfo> {

    @Override
    protected Class<GenericToolConfiguration> getToolConfigurationClass() {
        return GenericToolConfiguration.class;
    }

}
