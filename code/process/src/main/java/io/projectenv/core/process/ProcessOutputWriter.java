package io.projectenv.core.process;

import java.text.MessageFormat;

public interface ProcessOutputWriter {

    default void write(String outputPattern, Object... outputArgs) {
        write(MessageFormat.format(outputPattern, outputArgs));
    }

    void write(String output);

}
