package io.projectenv.core.tools.installer.impl;

import io.projectenv.core.configuration.GenericToolConfiguration;
import io.projectenv.core.tools.info.GenericToolInfo;

public class GenericToolInstaller extends AbstractProjectToolInstaller<GenericToolConfiguration, GenericToolInfo> {

    @Override
    protected Class<GenericToolConfiguration> getToolConfigurationClass() {
        return GenericToolConfiguration.class;
    }

}
