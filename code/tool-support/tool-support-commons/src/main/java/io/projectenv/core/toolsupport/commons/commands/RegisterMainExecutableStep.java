package io.projectenv.core.toolsupport.commons.commands;

import io.projectenv.core.toolsupport.spi.installation.ImmutableLocalToolInstallationDetails;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationDetails;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationStep;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationStepException;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class RegisterMainExecutableStep implements LocalToolInstallationStep {

    private final String rawExecutable;

    public RegisterMainExecutableStep(String rawExecutable) {
        this.rawExecutable = rawExecutable;
    }

    @Override
    public LocalToolInstallationDetails executeInstallStep(File installationRoot, LocalToolInstallationDetails intermediateInstallationDetails) throws LocalToolInstallationStepException {
        var executable = ProcessEnvironmentHelper.resolveExecutableFromPathElements(rawExecutable, intermediateInstallationDetails.getPathElements());

        return ImmutableLocalToolInstallationDetails.builder()
                .from(intermediateInstallationDetails)
                .primaryExecutable(executable)
                .build();
    }

    @Override
    public LocalToolInstallationDetails executeUpdateStep(File installationRoot, LocalToolInstallationDetails intermediateInstallationDetails) throws LocalToolInstallationStepException {
        return executeInstallStep(installationRoot, intermediateInstallationDetails);
    }

    @Override
    public void updateChecksum(MessageDigest digest) {
        digest.update(rawExecutable.getBytes(StandardCharsets.UTF_8));
    }

}
