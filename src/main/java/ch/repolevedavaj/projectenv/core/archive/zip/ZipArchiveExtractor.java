package ch.repolevedavaj.projectenv.core.archive.zip;

import ch.repolevedavaj.projectenv.core.archive.AbstractArchiveExtractor;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.lang3.SystemUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

public class ZipArchiveExtractor extends AbstractArchiveExtractor<ZipArchiveInputStream, ZipArchiveEntry> {

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
        int unixMode = archiveEntry.getUnixMode();
        // Zip files do not contain any permission information. Therefore, we need to make scripts executable.
        if (unixMode == 0 && archiveEntry.getName().endsWith(".sh") && SystemUtils.IS_OS_UNIX) {
            return 493;
        }

        return unixMode != 0 ? unixMode : null;
    }

}
