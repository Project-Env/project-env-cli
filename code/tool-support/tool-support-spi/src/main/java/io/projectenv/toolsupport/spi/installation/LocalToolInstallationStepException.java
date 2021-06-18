package io.projectenv.toolsupport.spi.installation;

public class LocalToolInstallationStepException extends Exception {

    public LocalToolInstallationStepException(String message, Throwable cause) {
        super(message, cause);
    }

    public LocalToolInstallationStepException(String message) {
        super(message);
    }

}
