package io.projectenv.core.common.lock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class LockFileHelperTest {

    @Test
    void testTryAcquireLockFile(@TempDir File tempDir) throws Exception {
        File targetLockFile = new File(tempDir, ".lock");

        // try to acquire lock file and check that it is successful
        LockFile lockFile = LockFileHelper.tryAcquireLockFile(targetLockFile, Duration.ofHours(1));
        assertThat(targetLockFile).exists();

        // check that a second try to acquire the lock file will end in a timeout
        assertThatExceptionOfType(TimeoutException.class)
                .isThrownBy(() -> LockFileHelper.tryAcquireLockFile(targetLockFile, Duration.ofMillis(100)))
                .withMessageStartingWith("timeout while trying to acquire lock file");

        // check that a thread waiting for the lock file can be interrupted
        AcquireLockFileRunnable acquireLockFileRunnable = new AcquireLockFileRunnable(targetLockFile, Duration.ofHours(1));
        Thread acquireLockFileThread = new Thread(acquireLockFileRunnable);
        acquireLockFileThread.start();
        acquireLockFileThread.interrupt();
        acquireLockFileThread.join();
        assertThat(acquireLockFileRunnable.lockFile).isNull();
        assertThat(acquireLockFileRunnable.exception)
                .hasCauseInstanceOf(InterruptedException.class)
                .hasMessage("tried to acquire lock files, but was interrupted");

        // check that the file is deleted after closing the lock file instance
        lockFile.close();
        assertThat(targetLockFile).doesNotExist();
    }

    static class AcquireLockFileRunnable implements Runnable {

        private final File targetLockFile;
        private final Duration timeout;

        private Exception exception;
        private LockFile lockFile;

        AcquireLockFileRunnable(File targetLockFile, Duration timeout) {
            this.targetLockFile = targetLockFile;
            this.timeout = timeout;
        }

        @Override
        public void run() {
            try {
                lockFile = LockFileHelper.tryAcquireLockFile(targetLockFile, timeout);
            } catch (Exception e) {
                exception = e;
            }
        }

    }

}
