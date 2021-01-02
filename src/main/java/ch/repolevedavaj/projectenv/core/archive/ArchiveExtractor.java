package ch.repolevedavaj.projectenv.core.archive;

import java.io.File;
import java.net.URI;

public interface ArchiveExtractor {

    boolean supportsArchive(URI archiveUri);

    void extractArchive(URI archiveUri, File targetDirectory) throws Exception;

}
