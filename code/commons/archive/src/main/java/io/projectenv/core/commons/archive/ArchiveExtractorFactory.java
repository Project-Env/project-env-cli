package io.projectenv.core.commons.archive;

import io.projectenv.core.commons.archive.impl.DefaultArchiveExtractor;

public final class ArchiveExtractorFactory {

    private ArchiveExtractorFactory() {
        // noop
    }

    public static ArchiveExtractor createArchiveExtractor() {
        return new DefaultArchiveExtractor();
    }

}
