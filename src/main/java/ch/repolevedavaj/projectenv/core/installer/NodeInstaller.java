package ch.repolevedavaj.projectenv.core.installer;

import ch.repolevedavaj.projectenv.core.ProjectToolType;
import ch.repolevedavaj.projectenv.core.configuration.NodeInstallationConfiguration;

import java.util.List;

public class NodeInstaller extends AbstractProjectToolInstaller<NodeInstallationConfiguration> {

    @Override
    protected ProjectToolType getProjectToolType() {
        return ProjectToolType.NODE;
    }

    @Override
    protected List<String> getAdditionalPathElements() {
        return List.of("/bin");
    }

    @Override
    protected String getPrimaryExecutableName() {
        return "node";
    }

}
