package io.projectenv.core.toolsupport.commons.commands;

import io.projectenv.core.commons.process.ProcessEnvironmentHelper;
import io.projectenv.core.commons.process.ProcessHelper;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationDetails;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationStep;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationStepException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.List;

public class ExecuteCommandStep implements LocalToolInstallationStep {

    private static final String PROJECT_ENV_TOOL_ROOT = "PROJECT_ENV_TOOL_ROOT";

    private final String rawCommand;
    private final File workingDirectory;

    public ExecuteCommandStep(String rawCommand, File workingDirectory) {
        this.rawCommand = rawCommand;
        this.workingDirectory = workingDirectory;
    }

    @Override
    public LocalToolInstallationDetails executeInstallStep(File installationRoot, LocalToolInstallationDetails intermediateInstallationDetails) throws LocalToolInstallationStepException {
        try {
            var processEnvironment = ProcessEnvironmentHelper.createProcessEnvironment(
                    intermediateInstallationDetails.getEnvironmentVariables(),
                    intermediateInstallationDetails.getPathElements());

            var rawPostInstallationCommandParts = List.of(StringUtils.split(rawCommand));
            if (rawPostInstallationCommandParts.isEmpty()) {
                throw new LocalToolInstallationStepException("Empty command");
            }

            var executable = getExecutableNameFromRawCommand(rawPostInstallationCommandParts);
            var parameters = getParametersFromRawCommand(rawPostInstallationCommandParts);

            var processBuilder = new ProcessBuilder();
            processBuilder.environment().putAll(processEnvironment);
            processBuilder.environment().put(PROJECT_ENV_TOOL_ROOT, getProjectEnvToolRoot(installationRoot, intermediateInstallationDetails));
            processBuilder.command().add(resolveExecutable(executable, intermediateInstallationDetails.getPathElements()));
            processBuilder.command().addAll(parameters);
            processBuilder.directory(workingDirectory);

            ProcessHelper.executeProcess(processBuilder);

            return intermediateInstallationDetails;
        } catch (IOException e) {
            throw new LocalToolInstallationStepException("Failed to execute step", e);
        }
    }
    @Override
    public LocalToolInstallationDetails executeUpdateStep(File installationRoot, LocalToolInstallationDetails intermediateInstallationDetails) {
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

    private String getProjectEnvToolRoot(File installationRoot, LocalToolInstallationDetails intermediateInstallationDetails) throws IOException {
        return intermediateInstallationDetails.getBinariesRoot().orElse(installationRoot).getCanonicalPath();
    }

    @Override
    public void updateChecksum(MessageDigest digest) {
        digest.update(rawCommand.getBytes(StandardCharsets.UTF_8));
    }

}
