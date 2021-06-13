package io.projectenv.core.toolsupport.commons.archive;

import io.projectenv.core.toolsupport.commons.archive.impl.DefaultArchiveExtractor;

public final class ArchiveExtractorFactory {

    private ArchiveExtractorFactory() {
        // noop
    }

    public static ArchiveExtractor createArchiveExtractor() {
        return new DefaultArchiveExtractor();
    }

}
