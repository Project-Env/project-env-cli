package io.projectenv.cli.integration.assertions;

import org.assertj.core.api.ListAssert;
import org.assertj.core.api.MapAssert;
import org.assertj.core.api.OptionalAssert;
import org.assertj.core.api.SoftAssertions;

import java.io.File;

public class GitAssertions extends AbstractToolInfoAssertions {

    public GitAssertions(SoftAssertions assertions) {
        super(assertions);
    }

    @Override
    protected void assertToolBinariesRoot(OptionalAssert<File> assertions) {
        assertions.isEmpty();
    }

    @Override
    protected void assertPrimaryExecutable(OptionalAssert<File> assertions) {
        assertions.isEmpty();
    }

    @Override
    protected void assertEnvironmentVariables(MapAssert<String, File> assertions) {
        assertions.isEmpty();
    }

    @Override
    protected void assertPathElements(ListAssert<File> assertions) {
        assertions.isEmpty();
    }

    @Override
    protected void assertHandledProjectResources(ListAssert<File> assertions) {
        assertions.singleElement().satisfies(filePathEndsWithCondition("git-hook"));
    }

    @Override
    protected void assertUnhandledProjectResources(MapAssert<String, File> assertions) {
        assertions.isEmpty();
    }

}
