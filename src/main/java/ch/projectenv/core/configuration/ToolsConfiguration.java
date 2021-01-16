package ch.projectenv.core.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Value.Immutable
@JsonSerialize(as = ImmutableToolsConfiguration.class)
@JsonDeserialize(as = ImmutableToolsConfiguration.class)
public interface ToolsConfiguration {

    @JsonProperty(required = true)
    String getToolsDirectory();

    @JsonProperty("jdk")
    Optional<JdkConfiguration> getJdkConfiguration();

    @JsonProperty("maven")
    Optional<MavenConfiguration> getMavenConfiguration();

    @JsonProperty("gradle")
    Optional<GradleConfiguration> getGradleConfiguration();

    @JsonProperty("node")
    Optional<NodeConfiguration> getNodeConfiguration();

    @JsonProperty("genericTools")
    List<GenericToolConfiguration> getGenericToolConfigurations();

    @JsonIgnore
    default List<ToolConfiguration> getAllToolConfigurations() {
        List<ToolConfiguration> toolConfigurations = new ArrayList<>();

        getJdkConfiguration().ifPresent(toolConfigurations::add);
        getMavenConfiguration().ifPresent(toolConfigurations::add);
        getGradleConfiguration().ifPresent(toolConfigurations::add);
        getNodeConfiguration().ifPresent(toolConfigurations::add);
        toolConfigurations.addAll(getGenericToolConfigurations());

        return toolConfigurations;
    }

}
