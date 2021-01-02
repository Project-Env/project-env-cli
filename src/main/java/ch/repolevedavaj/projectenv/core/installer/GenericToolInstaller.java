package ch.repolevedavaj.projectenv.core.installer;

import ch.repolevedavaj.projectenv.core.ProjectToolType;
import ch.repolevedavaj.projectenv.core.configuration.GenericToolInstallationConfiguration;

public class GenericToolInstaller extends AbstractProjectToolInstaller<GenericToolInstallationConfiguration> {

    @Override
    protected ProjectToolType getProjectToolType() {
        return ProjectToolType.OTHER;
    }

}
