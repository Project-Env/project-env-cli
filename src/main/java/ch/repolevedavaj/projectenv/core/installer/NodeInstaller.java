package ch.repolevedavaj.projectenv.core.installer;

import ch.repolevedavaj.projectenv.core.configuration.NodeConfiguration;
import ch.repolevedavaj.projectenv.core.toolinfo.NodeInfo;

public class NodeInstaller extends AbstractProjectToolInstaller<NodeConfiguration, NodeInfo> {

    @Override
    protected Class<NodeConfiguration> getToolConfigurationClass() {
        return NodeConfiguration.class;
    }

}
