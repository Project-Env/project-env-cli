package ch.repolevedavaj.projectenv.core.archive.zip;

import ch.repolevedavaj.projectenv.core.archive.AbstractArchiveExtractor;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

public class ZipArchiveExtractor extends AbstractArchiveExtractor<ZipArchiveInputStream, ZipArchiveEntry> {

    private static final String DEFAULT_SHELL_SCRIPT_EXTENSION = ".sh";
    private static final int DEFAULT_SHELL_SCRIPT_MODE = 493;

    @Override
    protected List<String> getSupportedArchiveExtensions() {
        return List.of(".zip");
    }

    @Override
    protected ZipArchiveInputStream createArchiveInputStream(URI archiveUri) throws Exception {
        InputStream zipArchiveInputStream = new BufferedInputStream(archiveUri.toURL().openStream());

        return new ZipArchiveInputStream(zipArchiveInputStream);
    }

    @Override
    protected boolean isSymbolicLink(ZipArchiveEntry archiveEntry) {
        return archiveEntry.isUnixSymlink();
    }

    @Override
    protected void createSymbolicLink(ZipArchiveEntry archiveEntry, File target, File targetDirectory) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Integer getMode(ZipArchiveEntry archiveEntry) {
        Integer unixMode = archiveEntry.getUnixMode() != 0 ? archiveEntry.getUnixMode() : null;

        // Zip files do not contain any permission information. Therefore, we need to make scripts executable.
        if (unixMode == null && isShellScript(archiveEntry)) {
            return DEFAULT_SHELL_SCRIPT_MODE;
        }

        return unixMode;
    }

    private boolean isShellScript(ZipArchiveEntry archiveEntry) {
        return archiveEntry.getName().endsWith(DEFAULT_SHELL_SCRIPT_EXTENSION);
    }

}
