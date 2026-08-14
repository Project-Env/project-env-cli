package io.projectenv.core.toolsupport.git;

import io.projectenv.core.toolsupport.spi.ImmutableToolSupportContext;
import io.projectenv.core.toolsupport.spi.ToolInfo;
import io.projectenv.core.toolsupport.spi.ToolSupportException;
import io.projectenv.core.toolsupport.spi.http.HttpClientProvider;
import io.projectenv.core.toolsupport.spi.index.ToolsIndexManager;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assumptions.abort;
import static org.mockito.Mockito.mock;

class GitSupportTest {

    private static final String HOOKS_SOURCE_DIRECTORY_NAME = "git-hooks";
    private static final String HOOK_NAME = "pre-commit";
    private static final String HOOK_CONTENT = "#!/bin/sh\necho hook\n";

    private final GitSupport gitSupport = new GitSupport();

    @Test
    void installsHooksIntoGitDirectoryOfPlainRepository(@TempDir File tempDirectory) throws Exception {
        var projectRoot = createDirectory(tempDirectory, "project");
        createHookSource(projectRoot);
        var gitDirectory = createDirectory(projectRoot, ".git");

        var toolInfo = prepareTool(projectRoot);

        assertHookInstalledInto(gitDirectory);
        assertThat(toolInfo.getHandledProjectResources())
                .containsExactly(hookSource(projectRoot));
    }

    @Test
    void installsHooksIntoDefaultGitDirectoryIfGitDirectoryIsMissing(@TempDir File tempDirectory) throws Exception {
        var projectRoot = createDirectory(tempDirectory, "project");
        createHookSource(projectRoot);

        prepareTool(projectRoot);

        assertHookInstalledInto(new File(projectRoot, ".git"));
    }

    @Test
    void installsHooksIntoCommonGitDirectoryOfLinkedWorktreeWithRelativeCommonDirectory(@TempDir File tempDirectory) throws Exception {
        var mainGitDirectory = createDirectory(tempDirectory, "main/.git");
        var worktreeGitDirectory = createDirectory(mainGitDirectory, "worktrees/feature");
        writeFile(new File(worktreeGitDirectory, "commondir"), "../..\n");

        var projectRoot = createDirectory(tempDirectory, "feature");
        createHookSource(projectRoot);
        writeGitFile(projectRoot, worktreeGitDirectory.getAbsolutePath());

        prepareTool(projectRoot);

        assertHookInstalledInto(mainGitDirectory);
    }

    @Test
    void installsHooksIntoCommonGitDirectoryOfLinkedWorktreeWithAbsoluteCommonDirectory(@TempDir File tempDirectory) throws Exception {
        var mainGitDirectory = createDirectory(tempDirectory, "main/.git");
        var worktreeGitDirectory = createDirectory(mainGitDirectory, "worktrees/feature");
        writeFile(new File(worktreeGitDirectory, "commondir"), mainGitDirectory.getAbsolutePath() + "\n");

        var projectRoot = createDirectory(tempDirectory, "feature");
        createHookSource(projectRoot);
        writeGitFile(projectRoot, worktreeGitDirectory.getAbsolutePath());

        prepareTool(projectRoot);

        assertHookInstalledInto(mainGitDirectory);
    }

    @Test
    void installsHooksIntoCommonGitDirectoryOfLinkedWorktreeReachedThroughASymlink(@TempDir File tempDirectory) throws Exception {
        var mainGitDirectory = createDirectory(tempDirectory, "repo/main/.git");
        var worktreeGitDirectory = createDirectory(mainGitDirectory, "worktrees/feature");
        writeFile(new File(worktreeGitDirectory, "commondir"), "../..\n");

        // The real worktree is nested deeper than the symlink which points to it, so resolving the
        // relative reference below lexically instead of letting the OS do it leaves the repository.
        var realProjectRoot = createDirectory(tempDirectory, "repo/actual/nested/feature");
        createHookSource(realProjectRoot);
        writeGitFile(realProjectRoot, "../../../main/.git/worktrees/feature");

        var projectRoot = createSymbolicLink(new File(tempDirectory, "repo/feature"), realProjectRoot);

        prepareTool(projectRoot);

        assertHookInstalledInto(mainGitDirectory);
    }

    @Test
    void installsHooksIntoReferencedGitDirectoryOfSubmodule(@TempDir File tempDirectory) throws Exception {
        var submoduleGitDirectory = createDirectory(tempDirectory, "outer/.git/modules/sub");

        var projectRoot = createDirectory(tempDirectory, "outer/sub");
        createHookSource(projectRoot);
        writeGitFile(projectRoot, "../.git/modules/sub");

        prepareTool(projectRoot);

        assertHookInstalledInto(submoduleGitDirectory);
    }

    @Test
    void installsNoHooksIfHooksSourceDirectoryIsEmpty(@TempDir File tempDirectory) throws Exception {
        var projectRoot = createDirectory(tempDirectory, "project");
        createDirectory(projectRoot, HOOKS_SOURCE_DIRECTORY_NAME);
        var gitDirectory = createDirectory(projectRoot, ".git");

        var toolInfo = prepareTool(projectRoot);

        assertThat(toolInfo.getHandledProjectResources()).isEmpty();
        assertThat(new File(gitDirectory, "hooks")).doesNotExist();
    }

    @Test
    void installsNoHooksIfHooksSourceDirectoryIsMissing(@TempDir File tempDirectory) throws Exception {
        var projectRoot = createDirectory(tempDirectory, "project");
        var gitDirectory = createDirectory(projectRoot, ".git");

        var toolInfo = prepareTool(projectRoot);

        assertThat(toolInfo.getHandledProjectResources()).isEmpty();
        assertThat(new File(gitDirectory, "hooks")).doesNotExist();
    }

    @Test
    void failsIfGitFileDoesNotReferenceAGitDirectory(@TempDir File tempDirectory) throws Exception {
        var projectRoot = createDirectory(tempDirectory, "project");
        createHookSource(projectRoot);
        writeFile(new File(projectRoot, ".git"), "not a git directory reference\n");

        assertThatExceptionOfType(ToolSupportException.class)
                .isThrownBy(() -> prepareTool(projectRoot))
                .withRootCauseInstanceOf(IOException.class);
    }

    @Test
    void failsIfReferencedGitDirectoryDoesNotExist(@TempDir File tempDirectory) throws Exception {
        var projectRoot = createDirectory(tempDirectory, "project");
        createHookSource(projectRoot);
        writeGitFile(projectRoot, "../does-not-exist");

        assertThatExceptionOfType(ToolSupportException.class)
                .isThrownBy(() -> prepareTool(projectRoot))
                .withRootCauseInstanceOf(IOException.class);

        assertThat(new File(tempDirectory, "does-not-exist")).doesNotExist();
    }

    private ToolInfo prepareTool(File projectRoot) throws ToolSupportException {
        var configuration = ImmutableGitConfiguration.builder()
                .hooksDirectory(HOOKS_SOURCE_DIRECTORY_NAME)
                .build();

        var context = ImmutableToolSupportContext.builder()
                .projectRoot(projectRoot)
                .localToolInstallationManager(mock(LocalToolInstallationManager.class))
                .toolsIndexManager(mock(ToolsIndexManager.class))
                .httpClientProvider(mock(HttpClientProvider.class))
                .build();

        return gitSupport.prepareTool(configuration, context);
    }

    private void assertHookInstalledInto(File expectedGitDirectory) {
        assertThat(new File(expectedGitDirectory, "hooks/" + HOOK_NAME))
                .isFile()
                .hasContent(HOOK_CONTENT);
    }

    private void createHookSource(File projectRoot) throws IOException {
        createDirectory(projectRoot, HOOKS_SOURCE_DIRECTORY_NAME);
        writeFile(hookSource(projectRoot), HOOK_CONTENT);
    }

    private File hookSource(File projectRoot) {
        return new File(projectRoot, HOOKS_SOURCE_DIRECTORY_NAME + "/" + HOOK_NAME);
    }

    private void writeGitFile(File projectRoot, String gitDirectoryReference) throws IOException {
        writeFile(new File(projectRoot, ".git"), "gitdir: " + gitDirectoryReference + "\n");
    }

    private File createDirectory(File parent, String relativePath) throws IOException {
        return Files.createDirectories(new File(parent, relativePath).toPath()).toFile();
    }

    private File createSymbolicLink(File link, File target) {
        try {
            return Files.createSymbolicLink(link.toPath(), target.toPath()).toFile();
        } catch (IOException | UnsupportedOperationException e) {
            return abort("Cannot create symbolic links on this system: " + e.getMessage());
        }
    }

    private void writeFile(File file, String content) throws IOException {
        Files.writeString(file.toPath(), content);
    }

}
