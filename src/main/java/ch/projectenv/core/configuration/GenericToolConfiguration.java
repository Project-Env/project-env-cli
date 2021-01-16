package ch.projectenv.core.configuration;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableGenericToolConfiguration.class)
@JsonDeserialize(as = ImmutableGenericToolConfiguration.class)
public interface GenericToolConfiguration extends ToolConfiguration {
}
