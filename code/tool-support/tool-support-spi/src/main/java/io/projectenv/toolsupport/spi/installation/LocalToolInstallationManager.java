package io.projectenv.toolsupport.spi.installation;

import java.util.List;

public interface LocalToolInstallationManager {

    LocalToolInstallationDetails installOrUpdateTool(String toolName, List<LocalToolInstallationStep> localToolInstallationSteps) throws LocalToolInstallationManagerException;

}
