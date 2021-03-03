package io.projectenv.core.tools.repository.impl.catalogue;


import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.projectenv.core.configuration.GitHooksConfiguration;
import io.projectenv.core.tools.info.GitHooksInfo;
import org.immutables.value.Value;

@Value.Immutable
@JsonTypeName(GitHooksEntry.JSON_TYPE)
@JsonSerialize(as = ImmutableGitHooksEntry.class)
@JsonDeserialize(as = ImmutableGitHooksEntry.class)
public interface GitHooksEntry extends ToolEntry {

    String JSON_TYPE = "gitHooks";

    @Override
    GitHooksConfiguration getToolConfiguration();

    @Override
    GitHooksInfo getToolInstallationInfo();

}

