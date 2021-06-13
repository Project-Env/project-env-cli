package io.projectenv.core.cli.integration.assertions;

import io.projectenv.core.toolsupport.api.ToolInfo;
import org.apache.commons.io.FilenameUtils;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.MapAssert;
import org.assertj.core.api.OptionalAssert;
import org.assertj.core.api.SoftAssertions;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.InstanceOfAssertFactories.*;

public abstract class AbstractToolInfoAssertions implements Consumer<List<ToolInfo>> {

    private final SoftAssertions assertions;

    AbstractToolInfoAssertions(SoftAssertions assertions) {
        this.assertions = assertions;
    }

    @Override
    public void accept(List<ToolInfo> toolInfo) {
        var toolInfoAssert = assertions.assertThat(toolInfo).singleElement();

        assertToolBinariesRoot(toolInfoAssert.extracting(ToolInfo::getToolBinariesRoot, optional(File.class)));
        assertPrimaryExecutable(toolInfoAssert.extracting(ToolInfo::getPrimaryExecutable, optional(File.class)));
        assertEnvironmentVariables(toolInfoAssert.extracting(ToolInfo::getEnvironmentVariables, map(String.class, File.class)));
        assertPathElements(toolInfoAssert.extracting(ToolInfo::getPathElements, list(File.class)));
        assertHandledProjectResources(toolInfoAssert.extracting(ToolInfo::getHandledProjectResources, list(File.class)));
        assertUnhandledProjectResources(toolInfoAssert.extracting(ToolInfo::getUnhandledProjectResources, map(String.class, File.class)));
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
