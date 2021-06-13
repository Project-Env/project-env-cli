package io.projectenv.cli;

public class ProjectEnvException extends Exception {

    public ProjectEnvException(String message) {
        super(message);
    }

    public ProjectEnvException(String message, Throwable cause) {
        super(message, cause);
    }

}
