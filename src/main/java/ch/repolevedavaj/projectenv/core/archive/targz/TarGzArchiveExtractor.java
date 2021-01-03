package ch.repolevedavaj.projectenv.core.archive.targz;

import ch.repolevedavaj.projectenv.core.archive.AbstractArchiveExtractor;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.util.List;

public class TarGzArchiveExtractor extends AbstractArchiveExtractor<TarArchiveInputStream, TarArchiveEntry> {

    @Override
    protected List<String> getSupportedArchiveExtensions() {
        return List.of(".tar.gz");
    }

    @Override
    protected TarArchiveInputStream createArchiveInputStream(URI archiveUri) throws Exception {
        InputStream tarGzArchiveInputStream = new BufferedInputStream(archiveUri.toURL().openStream());
        InputStream tarArchiveInputStream = new BufferedInputStream(new GzipCompressorInputStream(tarGzArchiveInputStream));

        return new TarArchiveInputStream(tarArchiveInputStream);
    }

    @Override
    protected boolean isSymbolicLink(TarArchiveEntry archiveEntry) {
        return archiveEntry.isSymbolicLink();
    }

    @Override
    protected void createSymbolicLink(TarArchiveEntry archiveEntry, File target, File targetDirectory) throws Exception {
        File linkDestination = new File(archiveEntry.getLinkName());
        checkThatPathIsInsideBasePath(new File(target.isDirectory() ? target : target.getParentFile(), archiveEntry.getLinkName()), targetDirectory);

        FileUtils.forceMkdirParent(target.getCanonicalFile());

        Files.createSymbolicLink(target.toPath(), linkDestination.toPath());
    }

    @Override
    protected Integer getMode(TarArchiveEntry archiveEntry) {
        return archiveEntry.getMode();
    }

}
