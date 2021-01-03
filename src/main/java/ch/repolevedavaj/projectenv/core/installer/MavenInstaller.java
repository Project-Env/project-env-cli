package ch.repolevedavaj.projectenv.core.installer;

import ch.repolevedavaj.projectenv.core.ProjectToolType;
import ch.repolevedavaj.projectenv.core.configuration.MavenInstallationConfiguration;

import java.util.List;

public class MavenInstaller extends AbstractProjectToolInstaller<MavenInstallationConfiguration> {

    @Override
    protected ProjectToolType getProjectToolType() {
        return ProjectToolType.MAVEN;
    }

    @Override
    protected List<String> getAdditionalPathElements() {
        return List.of("/bin");
    }

    @Override
    protected String getPrimaryExecutableName() {
        return "mvn";
    }

}
