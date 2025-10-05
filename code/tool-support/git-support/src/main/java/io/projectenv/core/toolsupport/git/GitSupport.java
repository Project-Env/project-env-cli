package io.projectenv.core.toolsupport.git;

import io.projectenv.core.toolsupport.spi.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GitSupport implements ToolSupport<GitConfiguration> {

    private static final String RELATIVE_GIT_HOOKS_PATH = ".git/hooks";

    @Override
    public String getToolIdentifier() {
        return "git";
    }

    @Override
    public String getDescription(GitConfiguration toolConfiguration) {
        return "Git hooks";
    }

    @Override
    public Class<GitConfiguration> getToolConfigurationClass() {
        return GitConfiguration.class;
    }

    @Override
    public ToolInfo prepareTool(GitConfiguration toolConfiguration, ToolSupportContext context) throws ToolSupportException {
        try {
            var gitHooksSourceDirectory = new File(context.getProjectRoot(), toolConfiguration.getHooksDirectory());
            var gitHooksTargetDirectory = new File(context.getProjectRoot(), RELATIVE_GIT_HOOKS_PATH);

            var gitHooks = getAllGitHooks(gitHooksSourceDirectory);
            for (var hook : gitHooks) {
                FileUtils.copyFileToDirectory(hook, gitHooksTargetDirectory);
            }

            return ImmutableToolInfo.builder()
                    .handledProjectResources(gitHooks)
                    .build();
        } catch (IOException e) {
            throw new ToolSupportException("Failed to copy Git hooks", e);
        }
    }

    private List<File> getAllGitHooks(File gitHooksDirectory) {
        return Optional.ofNullable(gitHooksDirectory.listFiles())
                .map(Arrays::asList)
                .orElse(Collections.emptyList());
    }

}
