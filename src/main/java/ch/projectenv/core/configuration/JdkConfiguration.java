package ch.projectenv.core.configuration;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableJdkConfiguration.class)
@JsonDeserialize(as = ImmutableJdkConfiguration.class)
public interface JdkConfiguration extends ToolConfiguration {
}
