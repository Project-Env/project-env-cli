package io.projectenv.core.tools.collector.impl;

import io.projectenv.core.common.OperatingSystem;
import io.projectenv.core.configuration.NodeConfiguration;
import io.projectenv.core.tools.collector.ToolInfoCollectorContext;
import io.projectenv.core.tools.info.ImmutableNodeInfo;
import io.projectenv.core.tools.info.NodeInfo;
import io.projectenv.core.tools.info.ToolInfo;

import java.util.List;

public class NodeInfoCollector extends AbstractToolInfoCollector<NodeConfiguration, NodeInfo> {

    @Override
    protected NodeInfo collectToolSpecificInfo(ToolInfo baseToolInfo, NodeConfiguration toolConfiguration, ToolInfoCollectorContext context) {
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
