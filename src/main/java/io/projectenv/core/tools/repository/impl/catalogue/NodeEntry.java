package io.projectenv.core.tools.repository.impl.catalogue;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.projectenv.core.configuration.NodeConfiguration;
import io.projectenv.core.tools.info.NodeInfo;
import org.immutables.value.Value;

@Value.Immutable
@JsonTypeName(NodeEntry.JSON_TYPE)
@JsonSerialize(as = ImmutableNodeEntry.class)
@JsonDeserialize(as = ImmutableNodeEntry.class)
public interface NodeEntry extends ToolEntry {

    String JSON_TYPE = "node";

    @Override
    NodeConfiguration getToolConfiguration();

    @Override
    NodeInfo getToolInstallationInfo();

}
