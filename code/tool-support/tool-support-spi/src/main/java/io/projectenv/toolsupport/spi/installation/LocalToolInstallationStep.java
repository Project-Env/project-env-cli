package io.projectenv.toolsupport.spi.installation;

import java.io.File;
import java.security.MessageDigest;

public interface LocalToolInstallationStep {

    LocalToolInstallationDetails executeInstallStep(File installationRoot, LocalToolInstallationDetails intermediateInstallationDetails) throws LocalToolInstallationStepException;

    LocalToolInstallationDetails executeUpdateStep(File installationRoot, LocalToolInstallationDetails intermediateInstallationDetails) throws LocalToolInstallationStepException;

    void updateChecksum(MessageDigest digest);

}
