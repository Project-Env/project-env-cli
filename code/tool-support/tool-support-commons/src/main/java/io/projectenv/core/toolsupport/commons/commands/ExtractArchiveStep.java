package io.projectenv.core.toolsupport.commons.commands;

import io.projectenv.core.toolsupport.commons.archive.ArchiveExtractorFactory;
import io.projectenv.core.toolsupport.spi.installation.ImmutableLocalToolInstallationDetails;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationDetails;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationStep;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationStepException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ExtractArchiveStep implements LocalToolInstallationStep {

    private final String rawArchiveUri;

    public ExtractArchiveStep(String rawArchiveUri) {
        this.rawArchiveUri = rawArchiveUri;
    }

    @Override
    public LocalToolInstallationDetails executeInstallStep(File installationRoot, LocalToolInstallationDetails intermediateInstallationDetails) throws LocalToolInstallationStepException {
        try {
            var archiveUri = URI.create(rawArchiveUri);

            var archiveName = FilenameUtils.getName(archiveUri.getPath());
            var tempArchive = Files.createTempFile(null, archiveName).toFile();
            try (var inputStream = new BufferedInputStream(archiveUri.toURL().openStream());
                 var outputStream = new FileOutputStream(tempArchive)) {

                IOUtils.copy(inputStream, outputStream);
            }

            ArchiveExtractorFactory.createArchiveExtractor().extractArchive(tempArchive, installationRoot);
            FileUtils.forceDelete(tempArchive);

            return updateToolInstallationDetails(installationRoot, intermediateInstallationDetails);
        } catch (IOException e) {
            throw new LocalToolInstallationStepException("failed to execute step", e);
        }
    }

    @Override
    public LocalToolInstallationDetails executeUpdateStep(File installationRoot, LocalToolInstallationDetails intermediateInstallationDetails) throws LocalToolInstallationStepException {
        return updateToolInstallationDetails(installationRoot, intermediateInstallationDetails);
    }

    private LocalToolInstallationDetails updateToolInstallationDetails(File installationRoot, LocalToolInstallationDetails intermediateInstallationDetails) {
        return ImmutableLocalToolInstallationDetails.builder()
                .from(intermediateInstallationDetails)
                .binariesRoot(getToolBinariesRoot(installationRoot))
                .build();
    }

    private File getToolBinariesRoot(File toolDirectory) {
        var files = Optional.ofNullable(toolDirectory.listFiles())
                .map(Arrays::asList)
                .orElse(List.of());

        if (files.size() == 1) {
            return getToolBinariesRoot(files.get(0));
        } else {
            return toolDirectory;
        }
    }

    @Override
    public void updateChecksum(MessageDigest digest) {
        digest.update(rawArchiveUri.getBytes(StandardCharsets.UTF_8));
    }

}
