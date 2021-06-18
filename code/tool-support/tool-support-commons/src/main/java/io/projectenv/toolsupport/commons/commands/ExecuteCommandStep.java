package io.projectenv.toolsupport.commons.commands;

import io.projectenv.process.ProcessHelper;
import io.projectenv.toolsupport.spi.installation.LocalToolInstallationDetails;
import io.projectenv.toolsupport.spi.installation.LocalToolInstallationStep;
import io.projectenv.toolsupport.spi.installation.LocalToolInstallationStepException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.List;

public class ExecuteCommandStep implements LocalToolInstallationStep {

    private final String rawCommand;

    public ExecuteCommandStep(String rawCommand) {
        this.rawCommand = rawCommand;
    }

    @Override
    public LocalToolInstallationDetails executeInstallStep(File installationRoot, LocalToolInstallationDetails intermediateInstallationDetails) throws LocalToolInstallationStepException {
        try {
            var processEnvironment = ProcessEnvironmentHelper.createProcessEnvironmentFromToolInfo(
                    intermediateInstallationDetails.getEnvironmentVariables(),
                    intermediateInstallationDetails.getPathElements());

            var rawPostInstallationCommandParts = List.of(StringUtils.split(rawCommand));
            if (rawPostInstallationCommandParts.isEmpty()) {
                throw new LocalToolInstallationStepException("empty command");
            }

            var executable = getExecutableNameFromRawCommand(rawPostInstallationCommandParts);
            var parameters = getParametersFromRawCommand(rawPostInstallationCommandParts);

            var processBuilder = new ProcessBuilder();
            processBuilder.environment().putAll(processEnvironment);
            processBuilder.command().add(resolveExecutable(executable, intermediateInstallationDetails.getPathElements()));
            processBuilder.command().addAll(parameters);
            processBuilder.directory(intermediateInstallationDetails.getBinariesRoot().orElse(installationRoot));

            ProcessHelper.startAndWaitFor(processBuilder);

            return intermediateInstallationDetails;
        } catch (IOException e) {
            throw new LocalToolInstallationStepException("failed to execute step", e);
        }
    }

    @Override
    public LocalToolInstallationDetails executeUpdateStep(File installationRoot, LocalToolInstallationDetails intermediateInstallationDetails) throws LocalToolInstallationStepException {
        return intermediateInstallationDetails;
    }

    private String getExecutableNameFromRawCommand(List<String> rawPostInstallationCommandParts) {
        return rawPostInstallationCommandParts.get(0);
    }

    private List<String> getParametersFromRawCommand(List<String> rawPostInstallationCommandParts) {
        if (rawPostInstallationCommandParts.size() > 1) {
            return rawPostInstallationCommandParts.subList(1, rawPostInstallationCommandParts.size());
        } else {
            return Collections.emptyList();
        }
    }

    private String resolveExecutable(String executableName, List<File> pathElements) {
        File executable = ProcessEnvironmentHelper.resolveExecutableFromPathElements(executableName, pathElements);
        if (executable != null) {
            return executable.getAbsolutePath();
        } else {
            return executableName;
        }
    }

    @Override
    public void updateChecksum(MessageDigest digest) {
        digest.update(rawCommand.getBytes(StandardCharsets.UTF_8));
    }

}
