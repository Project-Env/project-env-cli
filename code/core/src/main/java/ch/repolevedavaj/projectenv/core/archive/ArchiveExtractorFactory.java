package ch.repolevedavaj.projectenv.core.archive;

import ch.repolevedavaj.projectenv.core.archive.targz.TarGzArchiveExtractor;
import ch.repolevedavaj.projectenv.core.archive.zip.ZipArchiveExtractor;

import java.net.URI;
import java.util.List;

public final class ArchiveExtractorFactory {

    private static final List<ArchiveExtractor> ARCHIVE_EXTRACTORS = List.of(
            new TarGzArchiveExtractor(),
            new ZipArchiveExtractor()
    );

    public static ArchiveExtractor getArchiveExtractor(URI archiveUri) {
        return ARCHIVE_EXTRACTORS
                .stream()
                .filter(archiveExtractor -> archiveExtractor.supportsArchive(archiveUri))
                .findFirst()
                .orElseThrow();
    }

}
