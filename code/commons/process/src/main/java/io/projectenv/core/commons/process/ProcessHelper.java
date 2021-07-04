package io.projectenv.core.commons.process;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public final class ProcessHelper {

    private ProcessHelper() {
        // noop
    }

    public static int startAndWaitFor(ProcessBuilder processBuilder) throws IOException {
        try {
            var process = processBuilder.start();
            bindOutput(process);

            var terminated = process.waitFor(1, TimeUnit.HOURS);
            if (!terminated) {
                process.destroy();
            }

            return process.exitValue();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("interrupted while waiting for process termination");
        }
    }

    public static void bindOutput(Process process) {
        bindStdOutput(process);
        bindErrOutput(process);
    }

    public static void bindStdOutput(Process process) {
        new Thread(() -> {
            try (var scanner = new Scanner(process.getInputStream())) {
                while (scanner.hasNextLine()) {
                    ProcessOutputWriterAccessor.getProcessInfoWriter().write(scanner.nextLine());
                }
            }
        }).start();
    }

    public static void bindErrOutput(Process process) {
        new Thread(() -> {
            try (var scanner = new Scanner(process.getErrorStream())) {
                while (scanner.hasNextLine()) {
                    ProcessOutputWriterAccessor.getProcessInfoWriter().write(scanner.nextLine());
                }
            }
        }).start();
    }

}
