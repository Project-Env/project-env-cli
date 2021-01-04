package ch.repolevedavaj.projectenv.core.archive.accessor.tar;

import ch.repolevedavaj.projectenv.core.archive.accessor.ArchiveAccessor;
import ch.repolevedavaj.projectenv.core.archive.accessor.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.concurrent.LazyInitializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TarArchiveAccessor implements ArchiveAccessor {

    private final TarArchiveInputStream inputStream;

    public TarArchiveAccessor(TarArchiveInputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public ArchiveEntry getNextEntry() throws IOException {
        org.apache.commons.compress.archivers.tar.TarArchiveEntry entry;
        while ((entry = inputStream.getNextTarEntry()) != null) {
            if (inputStream.canReadEntryData(entry)) {
                return new TarArchiveEntry(entry);
            }
        }

        return null;
    }

    @Override
    public void close() throws Exception {
        inputStream.close();
    }

    private class TarArchiveEntry implements ArchiveEntry {

        private final org.apache.commons.compress.archivers.tar.TarArchiveEntry entry;
        private final LazyInitializer<byte[]> content;

        public TarArchiveEntry(org.apache.commons.compress.archivers.tar.TarArchiveEntry entry) {
            this.entry = entry;

            content = new LazyInitializer<>() {
                @Override
                protected byte[] initialize() {
                    try {
                        return IOUtils.toByteArray(inputStream);
                    } catch (IOException e) {
                        throw new RuntimeException("failed to read entry content", e);
                    }
                }
            };
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
            return entry.isSymbolicLink();
        }

        @Override
        public InputStream createInputStream() throws Exception {
            return new ByteArrayInputStream(content.get());
        }

        @Override
        public String getLinkName() {
            return entry.getLinkName();
        }

        @Override
        public Integer getMode() {
            return entry.getMode() != 0 ? entry.getMode() : null;
        }

    }

}
