package io.projectenv.core.toolsupport.git;

import io.projectenv.core.toolsupport.api.ImmutableToolInfo;
import io.projectenv.core.toolsupport.api.ToolInfo;
import io.projectenv.core.toolsupport.spi.ToolSupport;
import io.projectenv.core.toolsupport.spi.ToolSupportContext;
import io.projectenv.core.toolsupport.spi.ToolSupportException;
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
            throw new ToolSupportException("failed to copy Git hooks", e);
        }
    }

    private List<File> getAllGitHooks(File gitHooksDirectory) {
        return Optional.ofNullable(gitHooksDirectory.listFiles())
                .map(Arrays::asList)
                .orElse(Collections.emptyList());
    }

}
