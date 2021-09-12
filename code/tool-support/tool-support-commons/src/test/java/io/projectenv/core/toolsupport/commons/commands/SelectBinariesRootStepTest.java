package io.projectenv.core.toolsupport.commons.commands;

import io.projectenv.core.toolsupport.spi.installation.ImmutableLocalToolInstallationDetails;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class SelectBinariesRootStepTest {

    @Test
    void testExecuteInstallStepWithoutBinariesRoot() throws Exception {
        var installationRoot = new File("installationRoot");
        var installationDetails = ImmutableLocalToolInstallationDetails.builder().build();

        var modifiedInstallationDetails = new SelectBinariesRootStep("child")
                .executeInstallStep(installationRoot, installationDetails);

        assertThat(modifiedInstallationDetails.getBinariesRoot()).isPresent()
                .hasValueSatisfying((file) -> assertFilePath(file, "installationRoot/child"));
    }

    @Test
    void testExecuteInstallStepWithBinariesRoot() throws Exception {
        var installationRoot = new File("installationRoot");
        var installationDetails = ImmutableLocalToolInstallationDetails.builder()
                .binariesRoot(new File("binariesRoot"))
                .build();

        var modifiedInstallationDetails = new SelectBinariesRootStep("child")
                .executeInstallStep(installationRoot, installationDetails);

        assertThat(modifiedInstallationDetails.getBinariesRoot()).isPresent()
                .hasValueSatisfying((file) -> assertFilePath(file, "binariesRoot/child"));
    }

    @Test
    void testExecuteUpdateStepWithoutBinariesRoot() throws Exception {
        var installationRoot = new File("installationRoot");
        var installationDetails = ImmutableLocalToolInstallationDetails.builder().build();

        var modifiedInstallationDetails = new SelectBinariesRootStep("child")
                .executeUpdateStep(installationRoot, installationDetails);

        assertThat(modifiedInstallationDetails.getBinariesRoot()).isPresent()
                .hasValueSatisfying((file) -> assertFilePath(file, "installationRoot/child"));
    }

    @Test
    void testExecuteUpdateStepWithBinariesRoot() throws Exception {
        var installationRoot = new File("installationRoot");
        var installationDetails = ImmutableLocalToolInstallationDetails.builder()
                .binariesRoot(new File("binariesRoot"))
                .build();

        var modifiedInstallationDetails = new SelectBinariesRootStep("child")
                .executeUpdateStep(installationRoot, installationDetails);

        assertThat(modifiedInstallationDetails.getBinariesRoot()).isPresent()
                .hasValueSatisfying((file) -> assertFilePath(file, "binariesRoot/child"));
    }

    private void assertFilePath(File file, String expectedPath) {
        assertThat(file.getPath()).isEqualTo(expectedPath.replace('/', File.separatorChar));
    }

}