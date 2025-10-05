package io.projectenv.core.cli.installer;

import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationDetails;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationManagerException;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationStep;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationStepException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class DefaultLocalToolInstallationManagerTest {

    @Test
    void testCleanUpAfterException(@TempDir File tempDir) {
        var localToolInstallationManager = new DefaultLocalToolInstallationManager(tempDir);

        assertThatExceptionOfType(LocalToolInstallationManagerException.class)
                .isThrownBy(() -> localToolInstallationManager.installOrUpdateTool("test", List.of(new ExceptionThrowingLocalToolInstallationStep("test"))))
                .withMessage("Failed to install tool test");

        assertThat(tempDir).isEmptyDirectory();
    }

    private static class ExceptionThrowingLocalToolInstallationStep implements LocalToolInstallationStep {

        private final String toolName;

        ExceptionThrowingLocalToolInstallationStep(String toolName) {
            this.toolName = toolName;
        }

        @Override
        public LocalToolInstallationDetails executeInstallStep(File installationRoot, LocalToolInstallationDetails intermediateInstallationDetails) throws LocalToolInstallationStepException {
            throw new LocalToolInstallationStepException("Something went wrong");
        }

        @Override
        public LocalToolInstallationDetails executeUpdateStep(File installationRoot, LocalToolInstallationDetails intermediateInstallationDetails) throws LocalToolInstallationStepException {
            throw new LocalToolInstallationStepException("Something went wrong");
        }

        @Override
        public void updateChecksum(MessageDigest digest) {
            digest.update(toolName.getBytes(StandardCharsets.UTF_8));
        }

    }

}