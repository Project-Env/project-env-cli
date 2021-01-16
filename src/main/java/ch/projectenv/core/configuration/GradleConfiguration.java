package ch.projectenv.core.configuration;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableGradleConfiguration.class)
@JsonDeserialize(as = ImmutableGradleConfiguration.class)
public interface GradleConfiguration extends ToolConfiguration {
}
