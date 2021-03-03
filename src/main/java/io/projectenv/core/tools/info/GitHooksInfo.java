package io.projectenv.core.tools.info;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.io.File;

@Value.Immutable
@JsonSerialize(as = ImmutableGitHooksInfo.class)
@JsonDeserialize(as = ImmutableGitHooksInfo.class)
public interface GitHooksInfo extends ToolInfo {

    File getDirectory();

}
