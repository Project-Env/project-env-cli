package io.projectenv.core.commons.process;

import java.text.MessageFormat;

public final class ProcessOutput {

    private static boolean debugModeActive = false;

    private ProcessOutput() {
        // noop
    }

    public static void activateDebugMode() {
        debugModeActive = true;
    }

    public static void writeResult(String output) {
        System.out.println(output);
    }

    public static void writeInfoMessage(String message, Object... messageArguments) {
        System.err.println(MessageFormat.format(message, messageArguments));
    }
    
    public static void writeDebugMessage(String message, Object... messageArguments) {
        if (debugModeActive) {
            System.err.println(MessageFormat.format(message, messageArguments));
        }
    }

    public static void writeDebugMessage(Throwable throwable) {
        if (debugModeActive) {
            throwable.printStackTrace(System.err);
        }
    }

}
