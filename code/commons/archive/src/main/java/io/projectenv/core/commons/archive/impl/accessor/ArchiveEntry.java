package io.projectenv.core.commons.archive.impl.accessor;

import java.io.IOException;
import java.io.InputStream;

public interface ArchiveEntry {

    String getName();

    boolean isDirectory();

    boolean isSymbolicLink();

    InputStream createInputStream() throws IOException;

    String getLinkName() throws IOException;

    Integer getMode();

}
