package io.projectenv.core.toolsupport.commons.commands;

import io.projectenv.core.toolsupport.spi.installation.ImmutableLocalToolInstallationDetails;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationDetails;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationStep;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationStepException;

import java.io.File;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class FindBinariesRootStep implements LocalToolInstallationStep {

    private static final List<Pattern> IGNORED_FILES = List.of(
            // dot underscore files which are created on macOS systems and hold metadata
            Pattern.compile(".*/\\._.+")
    );

    @Override
    public LocalToolInstallationDetails executeInstallStep(File installationRoot, LocalToolInstallationDetails intermediateInstallationDetails) throws LocalToolInstallationStepException {
        var binariesRoot = intermediateInstallationDetails.getBinariesRoot().orElse(installationRoot);

        return ImmutableLocalToolInstallationDetails.builder()
                .from(intermediateInstallationDetails)
                .binariesRoot(getToolBinariesRoot(binariesRoot))
                .build();
    }

    @Override
    public LocalToolInstallationDetails executeUpdateStep(File installationRoot, LocalToolInstallationDetails intermediateInstallationDetails) throws LocalToolInstallationStepException {
        return executeInstallStep(installationRoot, intermediateInstallationDetails);
    }

    private File getToolBinariesRoot(File toolDirectory) {
        var files = Optional.ofNullable(toolDirectory.listFiles())
                .map(Arrays::asList)
                .orElse(List.of())
                .stream()
                .filter(this::shouldConsiderFile)
                .toList();

        if (files.size() == 1) {
            return getToolBinariesRoot(files.get(0));
        } else {
            return toolDirectory;
        }
    }

    private boolean shouldConsiderFile(File file) {
        return IGNORED_FILES.stream().noneMatch(pattern -> pattern.matcher(file.getName()).matches());
    }

    @Override
    public void updateChecksum(MessageDigest digest) {
        // noop
    }

}
