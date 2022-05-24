package io.projectenv.core.cli;

public class ProjectEnvException extends RuntimeException {

    public ProjectEnvException(String message) {
        super(message);
    }

    public ProjectEnvException(String message, Throwable cause) {
        super(message, cause);
    }

}
