package io.projectenv.core.configuration;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableGitHooksConfiguration.class)
@JsonDeserialize(as = ImmutableGitHooksConfiguration.class)
public interface GitHooksConfiguration extends ToolConfiguration {

    String getDirectory();

}
