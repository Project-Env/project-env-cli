package ch.projectenv.core.toolinfo.collector;

import ch.projectenv.core.toolinfo.NodeInfo;
import ch.projectenv.core.toolinfo.ToolInfo;
import ch.projectenv.core.common.OperatingSystem;
import ch.projectenv.core.configuration.NodeConfiguration;
import ch.projectenv.core.toolinfo.ImmutableNodeInfo;

import java.util.List;

public class NodeInfoCollector extends AbstractToolInfoCollector<NodeConfiguration, NodeInfo> {

    @Override
    protected NodeInfo collectToolSpecificInfo(ToolInfo baseToolInfo, NodeConfiguration toolConfiguration) {
        return ImmutableNodeInfo
                .builder()
                .from(baseToolInfo)
                .build();
    }

    @Override
    protected Class<NodeConfiguration> getToolConfigurationClass() {
        return NodeConfiguration.class;
    }

    @Override
    protected List<String> getAdditionalPathElements() {
        if (OperatingSystem.getCurrentOS() == OperatingSystem.WINDOWS) {
            return List.of("/");
        }

        return List.of("/bin");
    }

    @Override
    protected String getPrimaryExecutableName() {
        return "node";
    }

}
