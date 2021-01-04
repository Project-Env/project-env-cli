package ch.repolevedavaj.projectenv.core.installer;

import ch.repolevedavaj.projectenv.core.ProjectToolDetails;
import ch.repolevedavaj.projectenv.core.configuration.ConfigurationFactory;
import ch.repolevedavaj.projectenv.core.configuration.ProjectEnv;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ToolInstallerCollectionTest {

    @Test
    public void testInstallAllTools(@TempDir File toolsDirectory) throws Exception {
        ProjectEnv projectEnvConfiguration = ConfigurationFactory.createFromUrl(getClass().getResource("tool-installer-collection-test-project-env.xml"));

        List<ProjectToolDetails> toolDetails = ToolInstallerCollection.installAllTools(projectEnvConfiguration, toolsDirectory);
        assertThat(toolDetails).hasSize(5);
    }

}
