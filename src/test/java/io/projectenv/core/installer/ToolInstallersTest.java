package io.projectenv.core.installer;

import io.projectenv.core.configuration.ProjectEnvConfiguration;
import io.projectenv.core.configuration.ProjectEnvConfigurationFactory;
import io.projectenv.core.toolinfo.ToolInfo;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.assertj.core.api.Assertions.assertThat;

public class ToolInstallersTest {

    @Test
    public void testInstallAllTools(@TempDir File projectRoot) throws Exception {
        withEnvironmentVariable("USER", "user").execute(() -> {
            File settings = copyResourceToTarget("project-env.yml", projectRoot);
            copyResourceToTarget("settings.xml", projectRoot);
            copyResourceToTarget("settings-user.xml", projectRoot);

            ProjectEnvConfiguration projectEnvConfiguration = ProjectEnvConfigurationFactory.createFromFile(settings);

            List<ToolInfo> toolDetails = ToolInstallers.installAllTools(projectEnvConfiguration, projectRoot);
            assertThat(toolDetails).hasSize(5);
        });
    }

    private File copyResourceToTarget(String resource, File target) throws Exception {
        File resultingFile = new File(target, resource);

        try (InputStream inputStream = getClass().getResourceAsStream(resource);
             OutputStream outputStream = new FileOutputStream(resultingFile)) {
            IOUtils.copy(inputStream, outputStream);

            return resultingFile;
        }
    }

}
