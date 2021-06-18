package io.projectenv.toolsupport.commons.archive;

import io.projectenv.toolsupport.commons.archive.impl.DefaultArchiveExtractor;

import java.io.IOException;

public final class ArchiveExtractorFactory {

    private ArchiveExtractorFactory() {
        // noop
    }

    public static ArchiveExtractor createArchiveExtractor() throws IOException {
        return new DefaultArchiveExtractor();
    }

}
