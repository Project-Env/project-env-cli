package ch.repolevedavaj.projectenv.core.archive.accessor;

import java.io.IOException;

public interface ArchiveAccessor extends AutoCloseable {

    ArchiveEntry getNextEntry() throws IOException;

}
