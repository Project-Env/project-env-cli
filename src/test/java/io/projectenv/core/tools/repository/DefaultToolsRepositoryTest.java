package io.projectenv.core.tools.repository;

import io.projectenv.core.configuration.ProjectEnvConfiguration;
import io.projectenv.core.configuration.ProjectEnvConfigurationFactory;
import io.projectenv.core.configuration.ToolConfiguration;
import io.projectenv.core.tools.info.ToolInfo;
import org.apache.commons.io.FileUtils;
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

class DefaultToolsRepositoryTest {

    @Test
    void testRequestTools(@TempDir File projectRoot) throws Exception {
        withEnvironmentVariable("USER", "user").execute(() -> {
            File settings = copyResourceToTarget("project-env.yml", projectRoot);
            copyResourceToTarget("settings.xml", projectRoot);
            copyResourceToTarget("settings-user.xml", projectRoot);
            copyResourceToTarget("git-hook", new File(projectRoot, "hooks"));

            ProjectEnvConfiguration projectEnvConfiguration = ProjectEnvConfigurationFactory.createFromFile(settings);

            File repositoryRoot = new File(projectRoot, projectEnvConfiguration.getToolsConfiguration().getToolsDirectory());
            ToolsRepository toolRepository = ToolsRepositoryFactory.createToolRepository(repositoryRoot);

            List<ToolConfiguration> toolConfigurations = projectEnvConfiguration.getToolsConfiguration().getAllToolConfigurations();

            List<ToolInfo> firstToolDetails = toolRepository.requestTools(toolConfigurations, projectRoot);
            assertThat(firstToolDetails).hasSize(6);

            List<ToolInfo> secondToolInfos = toolRepository.requestTools(toolConfigurations, projectRoot);
            assertThat(secondToolInfos).containsAnyElementsOf(firstToolDetails);

            toolRepository.cleanAllToolsOfCurrentOSExcluding(toolConfigurations);
            List<ToolInfo> thirdToolInfos = toolRepository.requestTools(toolConfigurations, projectRoot);
            assertThat(thirdToolInfos).containsAnyElementsOf(firstToolDetails);
        });
    }

    private File copyResourceToTarget(String resource, File target) throws Exception {
        File resultingFile = new File(target, resource);
        FileUtils.forceMkdirParent(resultingFile);

        try (InputStream inputStream = getClass().getResourceAsStream(resource);
             OutputStream outputStream = new FileOutputStream(resultingFile)) {
            IOUtils.copy(inputStream, outputStream);

            return resultingFile;
        }
    }

}
