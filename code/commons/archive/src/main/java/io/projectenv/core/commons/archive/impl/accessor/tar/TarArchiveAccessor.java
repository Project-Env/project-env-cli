package io.projectenv.core.commons.archive.impl.accessor.tar;

import io.projectenv.core.commons.archive.impl.accessor.ArchiveAccessor;
import io.projectenv.core.commons.archive.impl.accessor.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.input.CloseShieldInputStream;

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
    public void close() throws IOException {
        inputStream.close();
    }

    private class TarArchiveEntry implements ArchiveEntry {

        private final org.apache.commons.compress.archivers.tar.TarArchiveEntry entry;

        public TarArchiveEntry(org.apache.commons.compress.archivers.tar.TarArchiveEntry entry) {
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
            return entry.isSymbolicLink();
        }

        @Override
        public InputStream createInputStream() {
            // We are using this class for test purposes in the Project-Env IntelliJ plugin.
            // But since the IntelliJ version, which is used for testing the plugin, is using
            // an older version of the commons-io dependency and the test framework doesn't
            // create separate classloaders during test, we use here the deprecated constructor.
            return new CloseShieldInputStream(inputStream);
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
