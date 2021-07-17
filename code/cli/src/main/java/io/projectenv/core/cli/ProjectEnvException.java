package io.projectenv.core.cli;

import java.io.IOException;

public class ProjectEnvException extends IOException {

    public ProjectEnvException(String message) {
        super(message);
    }

    public ProjectEnvException(String message, Throwable cause) {
        super(message, cause);
    }

}
