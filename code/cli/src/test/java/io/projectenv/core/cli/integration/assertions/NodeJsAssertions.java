package io.projectenv.core.cli.integration.assertions;

import io.projectenv.core.commons.system.OperatingSystem;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.MapAssert;
import org.assertj.core.api.OptionalAssert;
import org.assertj.core.api.SoftAssertions;

import java.io.File;

public class NodeJsAssertions extends AbstractToolInfoAssertions {

    public NodeJsAssertions(SoftAssertions assertions) {
        super(assertions);
    }

    @Override
    protected void assertToolBinariesRoot(OptionalAssert<File> assertions) {
        assertions.hasValueSatisfying(directoryPathEndsWithCondition(getBinariesRoot()));
    }

    @Override
    protected void assertPrimaryExecutable(OptionalAssert<File> assertions) {
        assertions.hasValueSatisfying(filePathEndsWithIgnoringFileExtensionCondition(getBinPath() + "/node"));
    }

    @Override
    protected void assertEnvironmentVariables(MapAssert<String, File> assertions) {
        assertions.isEmpty();
    }

    @Override
    protected void assertPathElements(ListAssert<File> assertions) {
        assertions.singleElement().satisfies(directoryPathEndsWithCondition(getBinPath()));
    }

    @Override
    protected void assertHandledProjectResources(ListAssert<File> assertions) {
        assertions.isEmpty();
    }

    @Override
    protected void assertUnhandledProjectResources(MapAssert<String, File> assertions) {
        assertions.isEmpty();
    }

    private String getBinariesRoot() {
        switch (OperatingSystem.getCurrentOperatingSystem()) {
            case MACOS:
                return "node-v14.15.3-darwin-x64";
            case WINDOWS:
                return "node-v14.15.3-win-x64";
            case LINUX:
                return "node-v14.15.3-linux-x64";
            default:
                throw new IllegalStateException("unsupported OS");
        }
    }

    private String getBinPath() {
        switch (OperatingSystem.getCurrentOperatingSystem()) {
            case MACOS:
            case LINUX:
                return getBinariesRoot() + "/bin";
            case WINDOWS:
                return getBinariesRoot();
            default:
                throw new IllegalStateException("unsupported OS");
        }
    }

}
