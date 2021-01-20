package io.projectenv.core.installer;

import io.projectenv.core.configuration.GenericToolConfiguration;
import io.projectenv.core.toolinfo.GenericToolInfo;

public class GenericToolInstaller extends AbstractProjectToolInstaller<GenericToolConfiguration, GenericToolInfo> {

    @Override
    protected Class<GenericToolConfiguration> getToolConfigurationClass() {
        return GenericToolConfiguration.class;
    }

}
