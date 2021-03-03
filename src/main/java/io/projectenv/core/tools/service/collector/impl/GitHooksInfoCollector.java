package io.projectenv.core.tools.service.collector.impl;

import io.projectenv.core.configuration.GitHooksConfiguration;
import io.projectenv.core.configuration.ToolConfiguration;
import io.projectenv.core.tools.info.GitHooksInfo;
import io.projectenv.core.tools.info.ImmutableGitHooksInfo;
import io.projectenv.core.tools.service.ToolSpecificServiceContext;
import io.projectenv.core.tools.service.collector.ToolInfoCollector;

import java.io.File;

public class GitHooksInfoCollector implements ToolInfoCollector<GitHooksConfiguration, GitHooksInfo> {

    @Override
    public GitHooksInfo collectToolInfo(GitHooksConfiguration toolConfiguration, ToolSpecificServiceContext context) {
        return ImmutableGitHooksInfo
                .builder()
                .directory(new File(context.getProjectRoot(), toolConfiguration.getDirectory()))
                .build();
    }

    @Override
    public boolean supportsTool(ToolConfiguration tool) {
        return tool instanceof GitHooksConfiguration;
    }

}
