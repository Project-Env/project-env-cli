package io.projectenv.core.tools.repository.impl.catalogue;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.projectenv.core.configuration.GenericToolConfiguration;
import io.projectenv.core.tools.info.GenericToolInfo;
import org.immutables.value.Value;

@Value.Immutable
@JsonTypeName(GenericToolEntry.JSON_TYPE)
@JsonSerialize(as = ImmutableGenericToolEntry.class)
@JsonDeserialize(as = ImmutableGenericToolEntry.class)
public interface GenericToolEntry extends ToolEntry {

    String JSON_TYPE = "generic";

    @Override
    GenericToolConfiguration getToolConfiguration();

    @Override
    GenericToolInfo getToolInstallationInfo();

}
