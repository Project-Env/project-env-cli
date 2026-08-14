package io.projectenv.core.toolsupport.git;

import io.projectenv.core.toolsupport.spi.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GitSupport implements ToolSupport<GitConfiguration> {

    private static final String RELATIVE_GIT_PATH = ".git";
    private static final String GIT_DIRECTORY_REFERENCE_PREFIX = "gitdir:";
    private static final String COMMON_DIRECTORY_MARKER_NAME = "commondir";
    private static final String RELATIVE_HOOKS_PATH = "hooks";

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
            var gitHooksTargetDirectory = new File(resolveGitDirectory(context.getProjectRoot()), RELATIVE_HOOKS_PATH);

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

    /**
     * Resolves the Git directory which holds the hooks.
     * <p>
     * In a plain repository this is {@code <projectRoot>/.git}. In a linked worktree and in a
     * submodule, {@code <projectRoot>/.git} is a file which references the real Git directory
     * instead. A linked worktree shares its hooks with the main worktree, which is why its common
     * Git directory is used. A submodule has no common Git directory and holds its own hooks.
     */
    private File resolveGitDirectory(File projectRoot) throws IOException {
        var gitPath = new File(projectRoot, RELATIVE_GIT_PATH);
        if (!gitPath.isFile()) {
            return gitPath;
        }

        var gitDirectory = resolvePath(projectRoot, readGitDirectoryReference(gitPath));
        if (!gitDirectory.isDirectory()) {
            throw new IOException("Git directory '" + gitDirectory + "' referenced by '" + gitPath + "' does not exist");
        }

        var commonDirectoryMarker = new File(gitDirectory, COMMON_DIRECTORY_MARKER_NAME);
        if (!commonDirectoryMarker.isFile()) {
            return gitDirectory;
        }

        return resolvePath(gitDirectory, readContent(commonDirectoryMarker));
    }

    private String readGitDirectoryReference(File gitFile) throws IOException {
        var content = readContent(gitFile);
        if (!content.startsWith(GIT_DIRECTORY_REFERENCE_PREFIX)) {
            throw new IOException("Cannot resolve Git directory referenced by '" + gitFile + "'");
        }

        return content.substring(GIT_DIRECTORY_REFERENCE_PREFIX.length()).trim();
    }

    private String readContent(File file) throws IOException {
        return Files.readString(file.toPath()).trim();
    }

    private File resolvePath(File baseDirectory, String path) {
        var resolvedPath = new File(path);
        return resolvedPath.isAbsolute() ? resolvedPath : new File(baseDirectory, path);
    }

    private List<File> getAllGitHooks(File gitHooksDirectory) {
        return Optional.ofNullable(gitHooksDirectory.listFiles())
                .map(Arrays::asList)
                .orElse(Collections.emptyList());
    }

}
