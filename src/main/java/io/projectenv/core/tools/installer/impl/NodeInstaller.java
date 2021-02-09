package io.projectenv.core.tools.installer.impl;

import io.projectenv.core.configuration.NodeConfiguration;
import io.projectenv.core.tools.info.NodeInfo;

public class NodeInstaller extends AbstractProjectToolInstaller<NodeConfiguration, NodeInfo> {

    @Override
    protected Class<NodeConfiguration> getToolConfigurationClass() {
        return NodeConfiguration.class;
    }

}
