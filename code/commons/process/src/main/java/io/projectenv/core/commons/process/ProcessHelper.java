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
            var process = processBuilder.start();

            var errOutput = new StringBuilder();
            if (!returnErrOutput) {
                bindErrOutput(process);
            } else {
                bindErrOutput(process, line -> errOutput.append(line).append('\n'));
            }

            var stdOutput = new StringBuilder();
            if (!returnStdOutput) {
                bindStdOutput(process);
            } else {
                bindStdOutput(process, line -> stdOutput.append(line).append('\n'));
            }

            var terminated = process.waitFor(1, TimeUnit.HOURS);
            if (!terminated) {
                process.destroy();
            }

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

    private static void bindStdOutput(Process process) {
        bindStdOutput(process, ProcessOutput::writeInfoMessage);
    }

    private static void bindStdOutput(Process process, ProcessOutputHandler handler) {
        bindOutput(process.getInputStream(), handler);
    }

    private static void bindErrOutput(Process process) {
        bindErrOutput(process, ProcessOutput::writeInfoMessage);
    }

    private static void bindErrOutput(Process process, ProcessOutputHandler handler) {
        bindOutput(process.getErrorStream(), handler);
    }

    private static void bindOutput(InputStream source, ProcessOutputHandler handler) {
        new Thread(() -> {
            try (var scanner = new Scanner(source)) {
                while (scanner.hasNextLine()) {
                    handler.handleOutput(scanner.nextLine());
                }
            }
        }).start();
    }

    private interface ProcessOutputHandler {

        void handleOutput(String line);

    }

}
