package ch.projectenv.core.installer;

import ch.projectenv.core.configuration.ProjectEnvConfigurationFactory;
import ch.projectenv.core.toolinfo.ToolInfo;
import ch.projectenv.core.configuration.ProjectEnvConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ToolInstallersTest {

    @Test
    public void testInstallAllTools(@TempDir File toolsDirectory) throws Exception {
        ProjectEnvConfiguration projectEnvConfiguration = ProjectEnvConfigurationFactory.createFromUrl(getClass().getResource("tool-installer-collection-test-project-env.yml"));

        List<ToolInfo> toolDetails = ToolInstallers.installAllTools(projectEnvConfiguration, toolsDirectory);
        assertThat(toolDetails).hasSize(5);
    }

}
