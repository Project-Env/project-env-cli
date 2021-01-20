package io.projectenv.core.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableProjectEnvConfiguration.class)
@JsonDeserialize(as = ImmutableProjectEnvConfiguration.class)
public interface ProjectEnvConfiguration {

    @JsonProperty(value = "tools", required = true)
    ToolsConfiguration getToolsConfiguration();

}
