package io.projectenv.core.common;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeoutException;

public final class LockFileHelper {

    private LockFileHelper() {
        // noop
    }

    public static LockFile tryAcquireLockFile(File lockFile, Duration timout) throws TimeoutException, IOException {
        long waitingSinceMillis = System.currentTimeMillis();
        while (lockFile.exists()) {
            long passedMillis = System.currentTimeMillis() - waitingSinceMillis;
            if (timout.minus(passedMillis, ChronoUnit.MILLIS).isNegative()) {
                throw new TimeoutException("failed to acquire lock file " + lockFile.getCanonicalPath());
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // re-set interrupt flag on thread
                Thread.currentThread().interrupt();
                throw new IOException("tried to acquire lock files, but was interrupted", e);
            }
        }

        if (!lockFile.createNewFile()) {
            throw new IOException("failed to create lock file " + lockFile.getCanonicalPath());
        }

        return new LockFile(lockFile);
    }

    public static class LockFile implements AutoCloseable {

        private final File file;

        public LockFile(File file) {
            this.file = file;
        }

        @Override
        public void close() throws Exception {
            FileUtils.forceDelete(file);
        }

    }

}
