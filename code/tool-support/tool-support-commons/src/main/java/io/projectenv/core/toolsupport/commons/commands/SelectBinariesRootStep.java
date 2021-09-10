package io.projectenv.core.toolsupport.commons.commands;

import io.projectenv.core.toolsupport.spi.installation.ImmutableLocalToolInstallationDetails;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationDetails;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationStep;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationStepException;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class SelectBinariesRootStep implements LocalToolInstallationStep {

    private final String rawBinariesRoot;

    public SelectBinariesRootStep(String rawBinariesRoot) {
        this.rawBinariesRoot = rawBinariesRoot;
    }

    @Override
    public LocalToolInstallationDetails executeInstallStep(File installationRoot, LocalToolInstallationDetails intermediateInstallationDetails) throws LocalToolInstallationStepException {
        var binariesRoot = new File(intermediateInstallationDetails.getBinariesRoot().orElse(installationRoot), rawBinariesRoot);

        return ImmutableLocalToolInstallationDetails.builder()
                .from(intermediateInstallationDetails)
                .binariesRoot(binariesRoot)
                .build();
    }

    @Override
    public LocalToolInstallationDetails executeUpdateStep(File installationRoot, LocalToolInstallationDetails intermediateInstallationDetails) throws LocalToolInstallationStepException {
        return executeInstallStep(installationRoot, intermediateInstallationDetails);
    }

    @Override
    public void updateChecksum(MessageDigest digest) {
        digest.update(rawBinariesRoot.getBytes(StandardCharsets.UTF_8));
    }

}
