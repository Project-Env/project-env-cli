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
        return switch (OperatingSystem.getCurrentOperatingSystem()) {
            case MACOS -> "node-v14.15.3-darwin-x64";
            case WINDOWS -> "node-v14.15.3-win-x64";
            case LINUX -> "node-v14.15.3-linux-x64";
        };
    }

    private String getBinPath() {
        return switch (OperatingSystem.getCurrentOperatingSystem()) {
            case MACOS, LINUX -> getBinariesRoot() + "/bin";
            case WINDOWS -> getBinariesRoot();
        };
    }

}
