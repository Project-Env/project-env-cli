package ch.repolevedavaj.projectenv.core.toolinfo.collector;

import ch.repolevedavaj.projectenv.core.common.OperatingSystem;
import ch.repolevedavaj.projectenv.core.configuration.NodeConfiguration;
import ch.repolevedavaj.projectenv.core.toolinfo.ImmutableNodeInfo;
import ch.repolevedavaj.projectenv.core.toolinfo.NodeInfo;
import ch.repolevedavaj.projectenv.core.toolinfo.ToolInfo;
import org.apache.commons.lang3.StringUtils;

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
            return List.of(StringUtils.EMPTY);
        }

        return List.of("/bin");
    }

    @Override
    protected String getPrimaryExecutableName() {
        return "node";
    }

}
