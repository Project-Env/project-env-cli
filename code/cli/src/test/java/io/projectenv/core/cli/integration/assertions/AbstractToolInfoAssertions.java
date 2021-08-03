package io.projectenv.core.cli.integration.assertions;

import io.projectenv.core.cli.api.ToolInfo;
import org.apache.commons.io.FilenameUtils;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.MapAssert;
import org.assertj.core.api.OptionalAssert;
import org.assertj.core.api.SoftAssertions;

import java.io.File;
import java.util.function.Consumer;

public abstract class AbstractToolInfoAssertions implements Consumer<ToolInfo> {

    private final SoftAssertions assertions;

    AbstractToolInfoAssertions(SoftAssertions assertions) {
        this.assertions = assertions;
    }

    @Override
    public void accept(ToolInfo toolInfo) {
        assertToolBinariesRoot(assertions.assertThat(toolInfo.getToolBinariesRoot()));
        assertPrimaryExecutable(assertions.assertThat(toolInfo.getPrimaryExecutable()));
        assertEnvironmentVariables(assertions.assertThat(toolInfo.getEnvironmentVariables()));
        assertPathElements(assertions.assertThat(toolInfo.getPathElements()));
        assertHandledProjectResources(assertions.assertThat(toolInfo.getHandledProjectResources()));
        assertUnhandledProjectResources(assertions.assertThat(toolInfo.getUnhandledProjectResources()));
    }

    protected abstract void assertToolBinariesRoot(OptionalAssert<File> assertions);

    protected abstract void assertPrimaryExecutable(OptionalAssert<File> assertions);

    protected abstract void assertEnvironmentVariables(MapAssert<String, File> assertions);

    protected abstract void assertPathElements(ListAssert<File> assertions);

    protected abstract void assertHandledProjectResources(ListAssert<File> assertions);

    protected abstract void assertUnhandledProjectResources(MapAssert<String, File> assertions);

    protected Consumer<File> filePathEndsWithCondition(String expectedSuffix) {
        return (File file) -> {
            assertions.assertThat(file).isFile().exists();
            assertions.assertThat(file.getPath()).endsWith(expectedSuffix);
        };
    }

    protected Consumer<File> filePathEndsWithIgnoringFileExtensionCondition(String expectedSuffix) {
        return (File file) -> {
            assertions.assertThat(file).isFile().exists();

            var filePathWithForwardSlashes = getPathWithForwardSlashes(file);
            assertions.assertThat(FilenameUtils.removeExtension(filePathWithForwardSlashes)).endsWith(expectedSuffix);
        };
    }

    protected Consumer<File> directoryPathEndsWithCondition(String expectedSuffix) {
        return (File directory) -> {
            assertions.assertThat(directory).isDirectory().exists();

            var filePathWithForwardSlashes = getPathWithForwardSlashes(directory);
            assertions.assertThat(filePathWithForwardSlashes).endsWith(expectedSuffix);
        };
    }

    private String getPathWithForwardSlashes(File file) {
        return file.getPath().replace("\\", "/");
    }

}
