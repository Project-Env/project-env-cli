package io.projectenv.core.commons.archive.impl.accessor;

import java.io.IOException;

public interface ArchiveAccessor extends AutoCloseable {

    ArchiveEntry getNextEntry() throws IOException;

    @Override
    void close() throws IOException;
}
