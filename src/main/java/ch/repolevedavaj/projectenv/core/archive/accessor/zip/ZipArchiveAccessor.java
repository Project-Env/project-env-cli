package ch.repolevedavaj.projectenv.core.archive.accessor.zip;

import ch.repolevedavaj.projectenv.core.archive.accessor.ArchiveAccessor;
import ch.repolevedavaj.projectenv.core.archive.accessor.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

public class ZipArchiveAccessor implements ArchiveAccessor {

    private final ZipFile zipFile;
    private final Enumeration<org.apache.commons.compress.archivers.zip.ZipArchiveEntry> entries;

    public ZipArchiveAccessor(ZipFile zipFile) {
        this.zipFile = zipFile;
        this.entries = zipFile.getEntries();
    }

    @Override
    public ArchiveEntry getNextEntry() throws IOException {
        while (entries.hasMoreElements()) {
            org.apache.commons.compress.archivers.zip.ZipArchiveEntry entry = entries.nextElement();
            if (zipFile.canReadEntryData(entry)) {
                return new ZipArchiveEntry(entries.nextElement());
            }
        }

        return null;
    }

    @Override
    public void close() throws Exception {
        // noop
    }

    private class ZipArchiveEntry implements ArchiveEntry {

        private final org.apache.commons.compress.archivers.zip.ZipArchiveEntry entry;

        public ZipArchiveEntry(org.apache.commons.compress.archivers.zip.ZipArchiveEntry entry) {
            this.entry = entry;
        }

        @Override
        public String getName() {
            return entry.getName();
        }

        @Override
        public boolean isDirectory() {
            return entry.isDirectory();
        }

        @Override
        public boolean isSymbolicLink() {
            return entry.isUnixSymlink();
        }

        @Override
        public InputStream createInputStream() throws Exception {
            return zipFile.getInputStream(entry);
        }

        @Override
        public String getLinkName() throws Exception {
            return IOUtils.toString(createInputStream(), zipFile.getEncoding());
        }

        @Override
        public Integer getMode() {
            return entry.getUnixMode() != 0 ? entry.getUnixMode() : null;
        }

    }

}
