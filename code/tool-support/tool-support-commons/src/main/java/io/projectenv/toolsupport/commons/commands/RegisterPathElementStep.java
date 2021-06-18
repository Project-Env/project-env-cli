package io.projectenv.toolsupport.commons.commands;

import io.projectenv.toolsupport.spi.installation.ImmutableLocalToolInstallationDetails;
import io.projectenv.toolsupport.spi.installation.LocalToolInstallationStep;
import io.projectenv.toolsupport.spi.installation.LocalToolInstallationStepException;
import io.projectenv.toolsupport.spi.installation.LocalToolInstallationDetails;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class RegisterPathElementStep implements LocalToolInstallationStep {

    private final String rawPathElement;

    public RegisterPathElementStep(String rawPathElement) {
        this.rawPathElement = rawPathElement;
    }

    @Override
    public LocalToolInstallationDetails executeInstallStep(File installationRoot, LocalToolInstallationDetails intermediateInstallationDetails) throws LocalToolInstallationStepException {
        var pathElement = new File(intermediateInstallationDetails.getBinariesRoot().orElse(installationRoot), rawPathElement);

        return ImmutableLocalToolInstallationDetails.builder()
                .from(intermediateInstallationDetails)
                .addPathElements(pathElement)
                .build();
    }

    @Override
    public LocalToolInstallationDetails executeUpdateStep(File installationRoot, LocalToolInstallationDetails intermediateInstallationDetails) throws LocalToolInstallationStepException {
        return executeInstallStep(installationRoot, intermediateInstallationDetails);
    }

    @Override
    public void updateChecksum(MessageDigest digest) {
        digest.update(rawPathElement.getBytes(StandardCharsets.UTF_8));
    }

}
