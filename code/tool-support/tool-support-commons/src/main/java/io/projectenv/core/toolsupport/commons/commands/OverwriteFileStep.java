package io.projectenv.core.toolsupport.commons.commands;

import io.projectenv.core.toolsupport.spi.installation.ImmutableLocalToolInstallationDetails;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationDetails;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationStep;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationStepException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class OverwriteFileStep implements LocalToolInstallationStep {

    private final File sourceRoot;
    private final String rawSource;
    private final String rawTarget;

    public OverwriteFileStep(File sourceRoot, String rawSource, String rawTarget) {
        this.sourceRoot = sourceRoot;
        this.rawSource = rawSource;
        this.rawTarget = rawTarget;
    }

    @Override
    public LocalToolInstallationDetails executeInstallStep(File installationRoot, LocalToolInstallationDetails intermediateInstallationDetails) throws LocalToolInstallationStepException {
        try {
            var source = new File(sourceRoot, rawSource);
            if (!source.exists()) {
                return intermediateInstallationDetails;
            }

            var target = new File(intermediateInstallationDetails.getBinariesRoot().orElse(installationRoot), rawTarget);
            FileUtils.forceMkdirParent(target);
            FileUtils.copyFile(source, target);

            return ImmutableLocalToolInstallationDetails.builder()
                    .from(intermediateInstallationDetails)
                    .addFileOverwrites(Pair.of(source, target))
                    .build();
        } catch (IOException e) {
            throw new LocalToolInstallationStepException("failed to execute step", e);
        }
    }

    @Override
    public LocalToolInstallationDetails executeUpdateStep(File installationRoot, LocalToolInstallationDetails intermediateInstallationDetails) throws LocalToolInstallationStepException {
        return executeInstallStep(installationRoot, intermediateInstallationDetails);
    }

    @Override
    public void updateChecksum(MessageDigest digest) {
        digest.update(rawSource.getBytes(StandardCharsets.UTF_8));
        digest.update(rawTarget.getBytes(StandardCharsets.UTF_8));
    }

}
