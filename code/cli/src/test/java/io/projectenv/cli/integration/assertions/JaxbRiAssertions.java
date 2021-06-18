package io.projectenv.cli.integration.assertions;

import org.assertj.core.api.ListAssert;
import org.assertj.core.api.MapAssert;
import org.assertj.core.api.OptionalAssert;
import org.assertj.core.api.SoftAssertions;

import java.io.File;

public class JaxbRiAssertions extends AbstractToolInfoAssertions {

    public JaxbRiAssertions(SoftAssertions assertions) {
        super(assertions);
    }

    @Override
    protected void assertToolBinariesRoot(OptionalAssert<File> assertions) {
        assertions.hasValueSatisfying(directoryPathEndsWithCondition(getBinariesRoot()));
    }

    @Override
    protected void assertPrimaryExecutable(OptionalAssert<File> assertions) {
        assertions.isEmpty();
    }

    @Override
    protected void assertEnvironmentVariables(MapAssert<String, File> assertions) {
        assertions.hasSize(1).hasEntrySatisfying("JAXB_HOME", directoryPathEndsWithCondition(getBinariesRoot()));
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
        return "jaxb-ri";
    }

}
