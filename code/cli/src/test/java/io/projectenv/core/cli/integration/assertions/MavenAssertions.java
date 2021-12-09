package io.projectenv.core.cli.integration.assertions;

import org.assertj.core.api.ListAssert;
import org.assertj.core.api.MapAssert;
import org.assertj.core.api.OptionalAssert;
import org.assertj.core.api.SoftAssertions;

import java.io.File;

public class MavenAssertions extends AbstractToolInfoAssertions {

    public MavenAssertions(SoftAssertions assertions) {
        super(assertions);
    }

    @Override
    protected void assertToolBinariesRoot(OptionalAssert<File> assertions) {
        assertions.hasValueSatisfying(directoryPathEndsWithCondition(getBinariesRoot()));
    }

    @Override
    protected void assertPrimaryExecutable(OptionalAssert<File> assertions) {
        assertions.hasValueSatisfying(filePathEndsWithIgnoringFileExtensionCondition(getBinariesRoot() + "/bin/mvn"));
    }

    @Override
    protected void assertEnvironmentVariables(MapAssert<String, File> assertions) {
        assertions.hasSize(1).hasEntrySatisfying("MAVEN_HOME", directoryPathEndsWithCondition(getBinariesRoot()));
    }

    @Override
    protected void assertPathElements(ListAssert<File> assertions) {
        assertions.singleElement().satisfies(directoryPathEndsWithCondition(getBinariesRoot() + "/bin"));
    }

    @Override
    protected void assertHandledProjectResources(ListAssert<File> assertions) {
        assertions.singleElement().satisfies(filePathEndsWithCondition("settings.xml"));
    }

    @Override
    protected void assertUnhandledProjectResources(MapAssert<String, File> assertions) {
        assertions.hasSize(1).hasEntrySatisfying("userSettingsFile", filePathEndsWithCondition("settings-user.xml"));
    }

    private String getBinariesRoot() {
        return "apache-maven-3.6.3";
    }

}
