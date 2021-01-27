package io.projectenv.core.common;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeoutException;

public class LockFileHelper {

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
                // ignore
            }
        }

        if (!lockFile.createNewFile()) {
            throw new IOException("failed to create lock file " + lockFile.getCanonicalPath());
        }

        return new LockFile(lockFile);
    }

    public static class LockFile implements AutoCloseable {

        private final File lockFile;

        public LockFile(File lockFile) {
            this.lockFile = lockFile;
        }

        @Override
        public void close() throws Exception {
            FileUtils.forceDelete(lockFile);
        }

    }

}
