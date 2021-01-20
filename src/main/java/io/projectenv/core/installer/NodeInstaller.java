package io.projectenv.core.installer;

import io.projectenv.core.toolinfo.NodeInfo;
import io.projectenv.core.configuration.NodeConfiguration;

public class NodeInstaller extends AbstractProjectToolInstaller<NodeConfiguration, NodeInfo> {

    @Override
    protected Class<NodeConfiguration> getToolConfigurationClass() {
        return NodeConfiguration.class;
    }

}
