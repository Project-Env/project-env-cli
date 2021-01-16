package ch.projectenv.core.archive.accessor;

import org.apache.commons.lang3.concurrent.ConcurrentException;

import java.io.IOException;
import java.io.InputStream;

public interface ArchiveEntry {

    String getName();

    boolean isDirectory();

    boolean isSymbolicLink();

    InputStream createInputStream() throws Exception;

    String getLinkName() throws Exception;

    Integer getMode();

}
