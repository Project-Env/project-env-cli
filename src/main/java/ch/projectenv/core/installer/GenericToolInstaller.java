package ch.projectenv.core.installer;

import ch.projectenv.core.configuration.GenericToolConfiguration;
import ch.projectenv.core.toolinfo.GenericToolInfo;

public class GenericToolInstaller extends AbstractProjectToolInstaller<GenericToolConfiguration, GenericToolInfo> {

    @Override
    protected Class<GenericToolConfiguration> getToolConfigurationClass() {
        return GenericToolConfiguration.class;
    }

}
