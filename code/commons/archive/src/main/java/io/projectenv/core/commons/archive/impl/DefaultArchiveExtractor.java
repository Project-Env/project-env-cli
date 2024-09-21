package io.projectenv.core.commons.archive.impl;

import io.projectenv.core.commons.archive.ArchiveExtractor;
import io.projectenv.core.commons.archive.impl.accessor.ArchiveAccessor;
import io.projectenv.core.commons.archive.impl.accessor.ArchiveAccessorFactory;
import io.projectenv.core.commons.archive.impl.accessor.ArchiveEntry;
import io.projectenv.core.commons.system.OperatingSystem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class DefaultArchiveExtractor implements ArchiveExtractor {

    private static final List<Pattern> IGNORED_ARCHIVE_ENTRIES = Collections.singletonList(
            // dot underscore files which are created on macOS systems and hold metadata
            Pattern.compile(".*/\\._.+")
    );

    public void extractArchive(File archive, File targetDirectory) throws IOException {
        try (ArchiveAccessor archiveAccessor = ArchiveAccessorFactory.createArchiveAccessor(archive)) {

            ArchiveEntry entry;

            while ((entry = archiveAccessor.getNextEntry()) != null) {
                if (!shouldExtractEntry(entry)) {
                    continue;
                }

                File target = new File(targetDirectory, entry.getName());
                checkThatPathIsInsideBasePath(target, targetDirectory);

                if (entry.isDirectory()) {
                    createDirectory(target);
                } else if (entry.isSymbolicLink()) {
                    createSymbolicLink(entry, target, targetDirectory);
                } else {
                    createFile(entry, target);
                }

                setPermissions(entry, target);
            }
        }
    }

    private boolean shouldExtractEntry(ArchiveEntry archiveEntry) {
        return IGNORED_ARCHIVE_ENTRIES.stream().noneMatch(pattern -> pattern.matcher(archiveEntry.getName()).matches());
    }

    private void checkThatPathIsInsideBasePath(File file, File baseDirectory) throws IOException {
        if (!file.getCanonicalPath().startsWith(baseDirectory.getCanonicalPath())) {
            throw new IllegalStateException("path " + file.getPath() + " is pointing to a location outside " + baseDirectory.getCanonicalPath());
        }
    }

    protected void createDirectory(File target) throws IOException {
        FileUtils.forceMkdir(target.getCanonicalFile());
    }

    protected void createSymbolicLink(ArchiveEntry archiveEntry, File target, File targetDirectory) throws IOException {
        File linkDestination = new File(archiveEntry.getLinkName());
        checkThatPathIsInsideBasePath(new File(target.isDirectory() ? target : target.getParentFile(), archiveEntry.getLinkName()), targetDirectory);

        FileUtils.forceMkdirParent(target.getCanonicalFile());

        Files.createSymbolicLink(target.toPath(), linkDestination.toPath());
    }

    private void createFile(ArchiveEntry archiveEntry, File target) throws IOException {
        FileUtils.forceMkdirParent(target.getCanonicalFile());

        try (InputStream inputStream = archiveEntry.createInputStream();
             OutputStream outputStream = new FileOutputStream(target)) {

            IOUtils.copy(inputStream, outputStream);
        }
    }

    private void setPermissions(ArchiveEntry archiveEntry, File target) throws IOException {
        // we do not set any permissions on Windows
        if (OperatingSystem.getCurrentOperatingSystem() == OperatingSystem.WINDOWS) {
            return;
        }

        if (archiveEntry.isSymbolicLink()) {
            return;
        }

        Integer mode = archiveEntry.getMode();
        if (mode != null) {
            Files.setPosixFilePermissions(target.toPath(), posixFilePermissionsFromMode(mode));
        }
    }

    private Set<PosixFilePermission> posixFilePermissionsFromMode(int decimalMode) {
        // to determine the Posix permission flags, we only need the last 12 bits
        int relevantPermissionBits = decimalMode & 0b111111111;

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
