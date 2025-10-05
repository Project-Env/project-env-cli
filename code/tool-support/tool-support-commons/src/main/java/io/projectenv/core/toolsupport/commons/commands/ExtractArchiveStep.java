package io.projectenv.core.toolsupport.commons.commands;

import io.projectenv.core.commons.archive.ArchiveExtractorFactory;
import io.projectenv.core.commons.process.ProcessOutput;
import io.projectenv.core.commons.system.OperatingSystem;
import io.projectenv.core.toolsupport.spi.http.HttpClientProvider;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationDetails;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationStep;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationStepException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

public class ExtractArchiveStep implements LocalToolInstallationStep {

    public static final Duration OTHER_PROCESS_ARCHIVE_DOWNLOAD_WAIT_LIMIT = Duration.ofMinutes(5);
    private final String rawArchiveUri;

    private final HttpClientProvider httpClientProvider;

    public ExtractArchiveStep(String rawArchiveUri, HttpClientProvider httpClientProvider) {
        this.rawArchiveUri = rawArchiveUri;
        this.httpClientProvider = httpClientProvider;
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
            ProcessOutput.writeDebugMessage("Using cached archive from {0}", archiveCachePath);
            return archiveCachePath;
        }

        Path archiveDownloadingPath = getArchiveDownloadingPath();
        if (Files.exists(archiveDownloadingPath)) {
            waitUntilArchiveHasBeenDownloadedByOtherProcess(archiveCachePath);
            ProcessOutput.writeDebugMessage("Using cached archive from {0}", archiveCachePath);
            return archiveCachePath;
        }

        ProcessOutput.writeDebugMessage("Downloading archive from {0}", rawArchiveUri);

        try {
            HttpResponse<InputStream> response = httpClientProvider.getHttpClient().send(
                    HttpRequest.newBuilder().uri(URI.create(rawArchiveUri)).GET().build(),
                    HttpResponse.BodyHandlers.ofInputStream()
            );

            if (response.statusCode() != 200) {
                throw new IOException("Failed to download archive, status code: " + response.statusCode());
            }
            try (var inputStream = response.body();
                 var outputStream = new FileOutputStream(archiveDownloadingPath.toFile())) {
                IOUtils.copy(inputStream, outputStream);
            }
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            deleteIfExists(archiveDownloadingPath);
            throw new LocalToolInstallationStepException("Failed to download archive from URI " + rawArchiveUri, e);
        }

        try {
            Files.move(archiveDownloadingPath, archiveCachePath);
            ProcessOutput.writeDebugMessage("Cached archive at {0}", archiveCachePath);
        } catch (IOException e) {
            deleteIfExists(archiveDownloadingPath);
            deleteIfExists(archiveCachePath);
            throw new LocalToolInstallationStepException("Failed to move downloaded archive from " + archiveDownloadingPath + " to " + archiveCachePath, e);
        }

        return archiveCachePath;
    }

    private Path getArchiveCachePath() throws LocalToolInstallationStepException {
        try {
            return getCacheDirectory().resolve(FilenameUtils.getName(rawArchiveUri));
        } catch (IOException e) {
            throw new LocalToolInstallationStepException("Failed to resolve archive cache path for URI " + rawArchiveUri, e);
        }
    }

    private Path getCacheDirectory() throws IOException {
        return Files.createDirectories(switch (OperatingSystem.getCurrentOperatingSystem()) {
            case MACOS -> Paths.get(System.getProperty("user.home"), "Library", "Caches", "Project-Env", "Downloads");
            case WINDOWS -> Paths.get(System.getenv("LOCALAPPDATA"), "Project-Env", "Cache", "Downloads");
            case LINUX -> Paths.get(System.getProperty("user.home"), ".cache", "project-env", "downloads");
        });
    }

    private Path getArchiveDownloadingPath() throws LocalToolInstallationStepException {
        Path archiveCachePath = getArchiveCachePath();
        return archiveCachePath.getParent().resolve(archiveCachePath.getFileName() + ".downloading");
    }

    private void waitUntilArchiveHasBeenDownloadedByOtherProcess(Path archivePath) throws LocalToolInstallationStepException {
        try (WatchService watchService = createWatchServiceForArchive(archivePath)) {
            Instant startTime = Instant.now();
            while (!Files.exists(archivePath) && Duration.between(startTime, Instant.now()).compareTo(OTHER_PROCESS_ARCHIVE_DOWNLOAD_WAIT_LIMIT) <= 0) {
                waitForArchiveDownloadEvents(watchService);
            }
        } catch (Exception e) {
            ProcessOutput.writeDebugMessage("Got exception while waiting for archive to be downloaded by other process", e);
        }

        if (!Files.exists(archivePath)) {
            throw new LocalToolInstallationStepException("Failed to wait for archive to be downloaded");
        }
    }

    private WatchService createWatchServiceForArchive(Path archiveDownloadingPath) throws IOException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        archiveDownloadingPath.getParent().register(watchService, ENTRY_CREATE);

        return watchService;
    }

    private void waitForArchiveDownloadEvents(WatchService watchService) throws Exception {
        WatchKey key = watchService.poll(OTHER_PROCESS_ARCHIVE_DOWNLOAD_WAIT_LIMIT.toMinutes(), TimeUnit.MINUTES);
        if (key != null) {
            key.pollEvents();
            if (!key.reset()) {
                throw new IOException("Watch key no more valid");
            }
        }
    }

    private void extractArchive(Path localArchivePath, File installationRoot) throws LocalToolInstallationStepException {
        try {
            ArchiveExtractorFactory.createArchiveExtractor().extractArchive(localArchivePath.toFile(), installationRoot);
        } catch (IOException e) {
            deleteIfExists(localArchivePath);
            throw new LocalToolInstallationStepException("Failed to extract archive from URI " + rawArchiveUri, e);
        }
    }

    private void deleteIfExists(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {

        }
    }

}
