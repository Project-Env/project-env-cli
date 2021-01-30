package io.projectenv.core.common.lock;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class LockFile implements AutoCloseable {

    private final File file;

    LockFile(File file) {
        this.file = file;
    }

    @Override
    public void close() throws IOException {
        FileUtils.forceDelete(file);
    }

}
