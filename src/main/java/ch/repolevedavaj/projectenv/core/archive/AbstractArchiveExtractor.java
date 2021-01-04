package ch.repolevedavaj.projectenv.core.archive;

import ch.repolevedavaj.projectenv.core.os.OS;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public abstract class AbstractArchiveExtractor<ArchiveInputStreamType extends ArchiveInputStream, ArchiveEntryType extends ArchiveEntry> implements ArchiveExtractor {

    private static final List<Pattern> IGNORED_ARCHIVE_ENTRIES = List.of(
            // dot underscore files which are created on macOS systems and hold metadata
            Pattern.compile(".*/\\._.+")
    );

    @Override
    public boolean supportsArchive(URI archiveUri) {
        return getSupportedArchiveExtensions()
                .stream()
                .anyMatch(extension -> StringUtils.endsWith(archiveUri.toString(), extension));
    }

    protected abstract List<String> getSupportedArchiveExtensions();

    @Override
    public void extractArchive(URI archiveUri, File targetDirectory) throws Exception {
        try (ArchiveInputStreamType archiveInputStream = createArchiveInputStream(archiveUri)) {
            extract(archiveInputStream, targetDirectory);
        }
    }

    protected abstract ArchiveInputStreamType createArchiveInputStream(URI archiveUri) throws Exception;

    protected void extract(ArchiveInputStreamType archiveInputStream, File targetDirectory) throws Exception {
        ArchiveEntryType entry;

        while ((entry = getNextEntry(archiveInputStream)) != null) {
            if (!archiveInputStream.canReadEntryData(entry)) {
                continue;
            }

            if(!shouldExtractEntry(entry)) {
                continue;
            }

            File target = new File(targetDirectory, entry.getName());
            checkThatPathIsInsideBasePath(target, targetDirectory);

            if (isDirectory(entry)) {
                createDirectory(target);
            } else if (isSymbolicLink(entry)) {
                createSymbolicLink(entry, target, targetDirectory);
            } else {
                createFile(archiveInputStream, target);
            }

            setPermissions(entry, target);
        }
    }

    protected boolean shouldExtractEntry(ArchiveEntryType archiveEntry) {
        return IGNORED_ARCHIVE_ENTRIES.stream().noneMatch(pattern -> pattern.matcher(archiveEntry.getName()).matches());
    }

    protected void checkThatPathIsInsideBasePath(File file, File baseDirectory) throws Exception {
        if (!file.getCanonicalPath().startsWith(baseDirectory.getCanonicalPath())) {
            throw new IllegalStateException("path " + file.getPath() + " is pointing to a location outside " + baseDirectory.getCanonicalPath());
        }
    }

    protected ArchiveEntryType getNextEntry(ArchiveInputStreamType archiveInputStream) throws Exception {
        return (ArchiveEntryType) archiveInputStream.getNextEntry();
    }

    protected boolean isDirectory(ArchiveEntryType archiveEntry) {
        return archiveEntry.isDirectory();
    }

    protected void createDirectory(File target) throws Exception {
        FileUtils.forceMkdir(target.getCanonicalFile());
    }

    protected abstract boolean isSymbolicLink(ArchiveEntryType archiveEntry);

    protected abstract void createSymbolicLink(ArchiveEntryType archiveEntry, File target, File targetDirectory) throws Exception;

    protected void createFile(ArchiveInputStreamType archiveInputStream, File target) throws Exception {
        FileUtils.forceMkdirParent(target.getCanonicalFile());

        try (OutputStream o = new FileOutputStream(target)) {
            IOUtils.copy(archiveInputStream, o);
        }
    }

    protected void setPermissions(ArchiveEntryType archiveEntry, File target) throws Exception {
        // we do not set any permissions on Windows
        if (OS.getCurrentOS() == OS.WINDOWS) {
            return;
        }

        if (isSymbolicLink(archiveEntry)) {
            return;
        }

        Integer mode = getMode(archiveEntry);
        if (mode != null) {
            Files.setPosixFilePermissions(target.toPath(), posixFilePermissionsFromMode(mode));
        }
    }

    protected abstract Integer getMode(ArchiveEntryType archiveEntry);

    private static Set<PosixFilePermission> posixFilePermissionsFromMode(int decimalMode) {
        // to determine the Posix permission flags, we only need the last 12 bits
        int relevantPermissionBits = decimalMode & 0b111111111111;

        char[] permissionFlags = Integer.toOctalString(relevantPermissionBits).toCharArray();

        StringBuilder posixPermissions = new StringBuilder();
        for (char permissionFlag : permissionFlags) {
            if ((permissionFlag & 0b100) != 0) {
                posixPermissions.append('r');
            } else {
                posixPermissions.append('-');
            }

            if ((permissionFlag & 0b010) != 0) {
                posixPermissions.append('w');
            } else {
                posixPermissions.append('-');
            }

            if ((permissionFlag & 0b001) != 0) {
                posixPermissions.append('x');
            } else {
                posixPermissions.append('-');
            }
        }

        return PosixFilePermissions.fromString(posixPermissions.toString());
    }

}
