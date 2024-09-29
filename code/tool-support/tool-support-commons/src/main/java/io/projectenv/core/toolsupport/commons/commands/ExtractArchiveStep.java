package io.projectenv.core.toolsupport.commons.commands;

import io.projectenv.core.commons.archive.ArchiveExtractorFactory;
import io.projectenv.core.commons.process.ProcessOutput;
import io.projectenv.core.commons.system.OperatingSystem;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationDetails;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationStep;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationStepException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;

public class ExtractArchiveStep implements LocalToolInstallationStep {

    private final String rawArchiveUri;

    public ExtractArchiveStep(String rawArchiveUri) {
        this.rawArchiveUri = rawArchiveUri;
    }

    @Override
    public LocalToolInstallationDetails executeInstallStep(File installationRoot, LocalToolInstallationDetails intermediateInstallationDetails) throws LocalToolInstallationStepException {
        Path localArchivePath = downloadArchive();
        extractArchive(localArchivePath, installationRoot);

        return intermediateInstallationDetails;
    }

    @Override
    public LocalToolInstallationDetails executeUpdateStep(File installationRoot, LocalToolInstallationDetails intermediateInstallationDetails) throws LocalToolInstallationStepException {
        return intermediateInstallationDetails;
    }

    @Override
    public void updateChecksum(MessageDigest digest) {
        digest.update(rawArchiveUri.getBytes(StandardCharsets.UTF_8));
    }

    private Path downloadArchive() throws LocalToolInstallationStepException {
        Path archiveCachePath = getArchiveCachePath();
        if (Files.exists(archiveCachePath)) {
            ProcessOutput.writeDebugMessage("using cached archive from {0}", archiveCachePath);
            return archiveCachePath;
        }

        ProcessOutput.writeDebugMessage("downloading archive from {0}", rawArchiveUri);
        try (var inputStream = URI.create(rawArchiveUri).toURL().openStream();
             var outputStream = new FileOutputStream(archiveCachePath.toFile())) {

            IOUtils.copy(inputStream, outputStream);

            ProcessOutput.writeDebugMessage("cached archive at {0}", archiveCachePath);
            return archiveCachePath;
        } catch (IOException e) {
            deleteIfExists(archiveCachePath);
            throw new LocalToolInstallationStepException("failed to download archive from URI " + rawArchiveUri, e);
        }
    }

    private Path getArchiveCachePath() throws LocalToolInstallationStepException {
        try {
            return getCacheDirectory().resolve(FilenameUtils.getName(rawArchiveUri));
        } catch (IOException e) {
            throw new LocalToolInstallationStepException("failed to resolve archive cache path for URI " + rawArchiveUri, e);
        }
    }

    private Path getCacheDirectory() throws IOException {
        return Files.createDirectories(switch (OperatingSystem.getCurrentOperatingSystem()) {
            case MACOS -> Paths.get(System.getProperty("user.home"), "Library", "Caches", "Project-Env", "Downloads");
            case WINDOWS -> Paths.get(System.getenv("LOCALAPPDATA"), "Project-Env", "Cache", "Downloads");
            case LINUX -> Paths.get(System.getProperty("user.home"), ".cache", "project-env", "downloads");
        });
    }

    private void extractArchive(Path localArchivePath, File installationRoot) throws LocalToolInstallationStepException {
        try {
            ArchiveExtractorFactory.createArchiveExtractor().extractArchive(localArchivePath.toFile(), installationRoot);
        } catch (IOException e) {
            deleteIfExists(localArchivePath);
            throw new LocalToolInstallationStepException("failed to extract archive from URI " + rawArchiveUri, e);
        }
    }

    private void deleteIfExists(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {

        }
    }

}
