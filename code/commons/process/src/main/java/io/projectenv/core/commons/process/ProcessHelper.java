package io.projectenv.core.commons.process;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public final class ProcessHelper {

    private ProcessHelper() {
        // noop
    }

    public static int executeProcess(ProcessBuilder processBuilder) throws IOException {
        return executeProcess(processBuilder, false).getExitCode();
    }

    public static ProcessResult executeProcess(ProcessBuilder processBuilder, boolean returnStdOutput) throws IOException {
        return executeProcess(processBuilder, returnStdOutput, false);
    }

    public static ProcessResult executeProcess(ProcessBuilder processBuilder, boolean returnStdOutput, boolean returnErrOutput) throws IOException {
        try {
            Process process = processBuilder.start();

            StringBuilder errOutput = new StringBuilder();
            Thread errOutputThread;
            if (!returnErrOutput) {
                errOutputThread = bindErrOutput(process);
            } else {
                errOutputThread = bindErrOutput(process, line -> errOutput.append(line).append('\n'));
            }

            StringBuilder stdOutput = new StringBuilder();
            Thread stdOutputThread;
            if (!returnStdOutput) {
                stdOutputThread = bindStdOutput(process);
            } else {
                stdOutputThread = bindStdOutput(process, line -> stdOutput.append(line).append('\n'));
            }

            boolean terminated = process.waitFor(1, TimeUnit.HOURS);
            if (!terminated) {
                process.destroy();
            }

            stdOutputThread.join();
            errOutputThread.join();

            return ImmutableProcessResult.builder()
                    .exitCode(process.exitValue())
                    .stdOutput(returnStdOutput ? Optional.of(stdOutput.toString()) : Optional.empty())
                    .errOutput(returnErrOutput ? Optional.of(errOutput.toString()) : Optional.empty())
                    .build();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("interrupted while waiting for process termination");
        }
    }

    private static Thread bindStdOutput(Process process) {
        return bindStdOutput(process, ProcessOutput::writeInfoMessage);
    }

    private static Thread bindStdOutput(Process process, ProcessOutputHandler handler) {
        return bindOutput(process.getInputStream(), handler);
    }

    private static Thread bindErrOutput(Process process) {
        return bindErrOutput(process, ProcessOutput::writeInfoMessage);
    }

    private static Thread bindErrOutput(Process process, ProcessOutputHandler handler) {
        return bindOutput(process.getErrorStream(), handler);
    }

    private static Thread bindOutput(InputStream source, ProcessOutputHandler handler) {
        Thread thread = new Thread(() -> {
            try (Scanner scanner = new Scanner(source)) {
                while (scanner.hasNextLine()) {
                    handler.handleOutput(scanner.nextLine());
                }
            }
        });
        thread.start();

        return thread;
    }

    private interface ProcessOutputHandler {

        void handleOutput(String line);

    }

}
