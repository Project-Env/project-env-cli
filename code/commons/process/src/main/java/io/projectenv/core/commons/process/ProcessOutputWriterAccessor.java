package io.projectenv.core.commons.process;

import java.io.PrintStream;

public final class ProcessOutputWriterAccessor {

    private static final ProcessOutputWriter PROCESS_RESULT_WRITER = new PrintStreamWriter(System.out);
    private static final ProcessOutputWriter PROCESS_INFO_WRITER = new PrintStreamWriter(System.err);

    private ProcessOutputWriterAccessor() {
        // noop
    }

    public static ProcessOutputWriter getProcessResultWriter() {
        return PROCESS_RESULT_WRITER;
    }

    public static ProcessOutputWriter getProcessInfoWriter() {
        return PROCESS_INFO_WRITER;
    }

    private static class PrintStreamWriter implements ProcessOutputWriter {

        private final PrintStream printStream;

        private PrintStreamWriter(PrintStream printStream) {
            this.printStream = printStream;
        }

        @Override
        public void write(String output) {
            printStream.println(output);
        }

        @Override
        public void write(Throwable throwable) {
            throwable.printStackTrace(printStream);
        }

    }

}
