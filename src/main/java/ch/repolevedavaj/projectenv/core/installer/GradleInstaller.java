package ch.repolevedavaj.projectenv.core.installer;

import ch.repolevedavaj.projectenv.core.ProjectToolType;
import ch.repolevedavaj.projectenv.core.configuration.MavenInstallationConfiguration;

import java.util.List;

public class GradleInstaller extends AbstractProjectToolInstaller<MavenInstallationConfiguration> {

    @Override
    protected ProjectToolType getProjectToolType() {
        return ProjectToolType.GRADLE;
    }

    @Override
    protected List<String> getAdditionalPathElements() {
        return List.of("/bin");
    }

}
