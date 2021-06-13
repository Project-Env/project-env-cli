package io.projectenv.core.toolsupport.commons.commands;

import io.projectenv.core.toolsupport.spi.installation.ImmutableLocalToolInstallationDetails;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationDetails;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationStep;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationStepException;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class RegisterEnvironmentVariableStep implements LocalToolInstallationStep {

    private final String environmentVariableName;
    private final String rawEnvironmentVariableValue;

    public RegisterEnvironmentVariableStep(String environmentVariableName, String rawEnvironmentVariableValue) {
        this.environmentVariableName = environmentVariableName;
        this.rawEnvironmentVariableValue = rawEnvironmentVariableValue;
    }

    @Override
    public LocalToolInstallationDetails executeInstallStep(File installationRoot, LocalToolInstallationDetails intermediateInstallationDetails) throws LocalToolInstallationStepException {
        var environmentVariableValue = new File(intermediateInstallationDetails.getBinariesRoot().orElse(installationRoot), rawEnvironmentVariableValue);

        return ImmutableLocalToolInstallationDetails.builder()
                .from(intermediateInstallationDetails)
                .putEnvironmentVariables(environmentVariableName, environmentVariableValue)
                .build();
    }

    @Override
    public LocalToolInstallationDetails executeUpdateStep(File installationRoot, LocalToolInstallationDetails intermediateInstallationDetails) throws LocalToolInstallationStepException {
        return executeInstallStep(installationRoot, intermediateInstallationDetails);
    }

    @Override
    public void updateChecksum(MessageDigest digest) {
        digest.update(environmentVariableName.getBytes(StandardCharsets.UTF_8));
        digest.update(rawEnvironmentVariableValue.getBytes(StandardCharsets.UTF_8));
    }

}
