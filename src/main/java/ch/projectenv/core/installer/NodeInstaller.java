package ch.projectenv.core.installer;

import ch.projectenv.core.toolinfo.NodeInfo;
import ch.projectenv.core.configuration.NodeConfiguration;

public class NodeInstaller extends AbstractProjectToolInstaller<NodeConfiguration, NodeInfo> {

    @Override
    protected Class<NodeConfiguration> getToolConfigurationClass() {
        return NodeConfiguration.class;
    }

}
