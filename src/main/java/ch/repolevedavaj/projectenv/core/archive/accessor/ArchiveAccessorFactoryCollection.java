package ch.repolevedavaj.projectenv.core.archive.accessor;

import ch.repolevedavaj.projectenv.core.archive.accessor.tar.TarArchiveAccessor;
import ch.repolevedavaj.projectenv.core.archive.accessor.zip.ZipArchiveAccessor;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

public class ArchiveAccessorFactoryCollection {

    private static final Map<String, ArchiveAccessorFactory> FACTORIES = Map.of(
            ".tar.gz", ArchiveAccessorFactoryCollection::createTarGzArchiveAccessor,
            ".tar.xz", ArchiveAccessorFactoryCollection::createTarXzArchiveAccessor,
            ".tar", ArchiveAccessorFactoryCollection::createTarArchiveAccessor,
            ".zip", ArchiveAccessorFactoryCollection::createZipArchiveAccessor
    );

    public static ArchiveAccessor createArchiveAccessor(File archive) throws Exception {
        for (Map.Entry<String, ArchiveAccessorFactory> factoryEntry : FACTORIES.entrySet()) {
            if (archive.getName().toLowerCase().endsWith(factoryEntry.getKey())) {
                return factoryEntry.getValue().createArchiveAccessor(archive);
            }
        }

        throw new IllegalArgumentException("unsupported archive " + archive.getName());
    }

    private static ArchiveAccessor createZipArchiveAccessor(File archive) throws Exception {
        ZipFile zipFile = new ZipFile(archive);

        return new ZipArchiveAccessor(zipFile);
    }

    private static ArchiveAccessor createTarXzArchiveAccessor(File archive) throws Exception {
        InputStream originalInputStream = new BufferedInputStream(new FileInputStream(archive));
        InputStream tarInputStream = new BufferedInputStream(new XZCompressorInputStream(originalInputStream));

        return createTarArchiveAccessor(tarInputStream);
    }

    private static ArchiveAccessor createTarGzArchiveAccessor(File archive) throws Exception {
        InputStream originalInputStream = new BufferedInputStream(new FileInputStream(archive));
        InputStream tarInputStream = new BufferedInputStream(new GzipCompressorInputStream(originalInputStream));

        return createTarArchiveAccessor(tarInputStream);
    }

    private static ArchiveAccessor createTarArchiveAccessor(File archive) throws Exception {
        InputStream originalInputStream = new BufferedInputStream(new FileInputStream(archive));

        return createTarArchiveAccessor(originalInputStream);
    }

    private static ArchiveAccessor createTarArchiveAccessor(InputStream tarInputStream) throws Exception {
        TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(tarInputStream);

        return new TarArchiveAccessor(tarArchiveInputStream);
    }

    private interface ArchiveAccessorFactory {

        ArchiveAccessor createArchiveAccessor(File archive) throws Exception;

    }


}
