package io.projectenv.core.cli.integration.assertions;

import io.projectenv.core.toolsupport.commons.system.OperatingSystem;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.MapAssert;
import org.assertj.core.api.OptionalAssert;
import org.assertj.core.api.SoftAssertions;

import java.io.File;

public class JdkAssertions extends AbstractToolInfoAssertions {

    public JdkAssertions(SoftAssertions assertions) {
        super(assertions);
    }

    @Override
    protected void assertToolBinariesRoot(OptionalAssert<File> assertions) {
        assertions.hasValueSatisfying(directoryPathEndsWithCondition(getBinariesRoot()));
    }

    @Override
    protected void assertPrimaryExecutable(OptionalAssert<File> assertions) {
        assertions.hasValueSatisfying(filePathEndsWithIgnoringFileExtensionCondition(getBinariesRoot() + "/bin/java"));
    }

    @Override
    protected void assertEnvironmentVariables(MapAssert<String, File> assertions) {
        assertions.hasSize(1).hasEntrySatisfying("JAVA_HOME", directoryPathEndsWithCondition(getBinariesRoot()));
    }

    @Override
    protected void assertPathElements(ListAssert<File> assertions) {
        assertions.singleElement().satisfies(directoryPathEndsWithCondition(getBinariesRoot() + "/bin"));
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
        if (OperatingSystem.getCurrentOperatingSystem() == OperatingSystem.MACOS) {
            return "graalvm-ce-java11-20.3.0/Contents/Home";
        } else {
            return "graalvm-ce-java11-20.3.0";
        }
    }

}
