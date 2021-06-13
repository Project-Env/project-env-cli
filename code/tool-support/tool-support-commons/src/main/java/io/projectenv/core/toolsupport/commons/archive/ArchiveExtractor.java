package io.projectenv.core.toolsupport.commons.archive;

import java.io.File;
import java.io.IOException;

public interface ArchiveExtractor {

    void extractArchive(File archive, File targetDirectory) throws IOException;

}
