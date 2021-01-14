package ch.repolevedavaj.projectenv.core.configuration;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableNodeConfiguration.class)
@JsonDeserialize(as = ImmutableNodeConfiguration.class)
public interface NodeConfiguration extends ToolConfiguration {
}
