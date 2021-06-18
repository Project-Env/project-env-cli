package io.projectenv.cli.installer;

import io.projectenv.cli.installer.lock.LockFile;
import io.projectenv.cli.installer.lock.LockFileHelper;
import io.projectenv.toolsupport.spi.installation.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class DefaultLocalToolInstallationManager implements LocalToolInstallationManager {

    private static final String DEFAULT_LOCK_FILENAME = ".lock";
    private static final Duration DEFAULT_LOCK_ACQUIRING_TIMEOUT = Duration.ofMinutes(5);

    private final File toolsRoot;

    public DefaultLocalToolInstallationManager(File toolsRoot) {
        this.toolsRoot = toolsRoot;
    }

    @Override
    public LocalToolInstallationDetails installOrUpdateTool(String toolName, List<LocalToolInstallationStep> localToolInstallationSteps) throws LocalToolInstallationManagerException {
        try (var ignored = tryAcquireLockFile()) {
            var toolIdentifier = createToolIdentifier(toolName, localToolInstallationSteps);
            var toolInstallationRoot = resolveToolInstallationRoot(toolIdentifier);
            if (toolInstallationRoot.exists()) {
                return updateTool(toolInstallationRoot, localToolInstallationSteps);
            } else {
                FileUtils.forceMkdir(toolInstallationRoot);
                return installTool(toolInstallationRoot, localToolInstallationSteps);
            }
        } catch (NoSuchAlgorithmException | IOException | LocalToolInstallationStepException | TimeoutException e) {
            throw new LocalToolInstallationManagerException("failed to install tool", e);
        }
    }

    private String createToolIdentifier(String toolName, List<LocalToolInstallationStep> localToolInstallationSteps) throws NoSuchAlgorithmException {
        var messageDigest = MessageDigest.getInstance("SHA-256");
        for (var installationStep : localToolInstallationSteps) {
            installationStep.updateChecksum(messageDigest);
        }

        var toolInstallationChecksum = Base64.getEncoder()
                .withoutPadding()
                .encodeToString(messageDigest.digest())
                .replace("/", "_")
                .replace("+", "-");

        return MessageFormat.format("{0}-{1}", toolName, toolInstallationChecksum);
    }

    private File resolveToolInstallationRoot(String toolIdentifier) {
        return new File(toolsRoot, toolIdentifier);
    }

    private LocalToolInstallationDetails installTool(File toolInstallationRoot, List<LocalToolInstallationStep> localToolInstallationSteps) throws LocalToolInstallationStepException {
        LocalToolInstallationDetails localToolInstallationDetails = ImmutableLocalToolInstallationDetails.builder().build();
        for (var installationStep : localToolInstallationSteps) {
            localToolInstallationDetails = installationStep.executeInstallStep(toolInstallationRoot, localToolInstallationDetails);
        }

        return localToolInstallationDetails;
    }

    private LocalToolInstallationDetails updateTool(File toolInstallationRoot, List<LocalToolInstallationStep> localToolInstallationSteps) throws LocalToolInstallationStepException, IOException, TimeoutException {
        return executeAllSteps(toolInstallationRoot, localToolInstallationSteps, LocalToolInstallationStep::executeUpdateStep);
    }

    private LocalToolInstallationDetails executeAllSteps(File toolInstallationRoot, List<LocalToolInstallationStep> localToolInstallationSteps, StepExecutor executor) throws LocalToolInstallationStepException, IOException, TimeoutException {
        LocalToolInstallationDetails localToolInstallationDetails = ImmutableLocalToolInstallationDetails.builder().build();
        for (var installationStep : localToolInstallationSteps) {
            localToolInstallationDetails = executor.executeStep(installationStep, toolInstallationRoot, localToolInstallationDetails);
        }

        return localToolInstallationDetails;
    }

    private LockFile tryAcquireLockFile() throws IOException, TimeoutException {
        var lockFile = new File(toolsRoot, DEFAULT_LOCK_FILENAME);

        return LockFileHelper.tryAcquireLockFile(lockFile, DEFAULT_LOCK_ACQUIRING_TIMEOUT);
    }

    private interface StepExecutor {

        LocalToolInstallationDetails executeStep(LocalToolInstallationStep step, File installationRoot, LocalToolInstallationDetails intermediateInstallationDetails) throws LocalToolInstallationStepException;

    }

}
