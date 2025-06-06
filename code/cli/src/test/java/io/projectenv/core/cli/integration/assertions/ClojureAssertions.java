package io.projectenv.core.cli.integration.assertions;

import io.projectenv.core.commons.system.OperatingSystem;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.MapAssert;
import org.assertj.core.api.OptionalAssert;
import org.assertj.core.api.SoftAssertions;

import java.io.File;

public class ClojureAssertions extends AbstractToolInfoAssertions {

    public ClojureAssertions(SoftAssertions assertions) {
        super(assertions);
    }

    @Override
    protected void assertToolBinariesRoot(OptionalAssert<File> assertions) {
        assertions.hasValueSatisfying(directoryPathEndsWithCondition(getBinariesRoot()));
    }

    @Override
    protected void assertPrimaryExecutable(OptionalAssert<File> assertions) {
        if (OperatingSystem.getCurrentOperatingSystem() == OperatingSystem.WINDOWS) {
            assertions.isEmpty();
        } else {
            assertions.hasValueSatisfying(filePathEndsWithIgnoringFileExtensionCondition(getBinPath() + "/clojure"));
        }
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
            case MACOS, LINUX -> "clojure-tools";
            case WINDOWS -> "ClojureTools";
        };
    }

    private String getBinPath() {
        return switch (OperatingSystem.getCurrentOperatingSystem()) {
            case MACOS, LINUX -> getBinariesRoot() + "/bin";
            case WINDOWS -> getBinariesRoot();
        };
    }

}
