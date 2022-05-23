package io.projectenv.core.toolsupport.spi;

public class ToolSupportException extends RuntimeException {
    public ToolSupportException(String message) {
        super(message);
    }

    public ToolSupportException(String message, Throwable cause) {
        super(message, cause);
    }

}
