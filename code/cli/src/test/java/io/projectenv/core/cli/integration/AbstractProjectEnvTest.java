package io.projectenv.core.cli.integration;

import com.google.gson.reflect.TypeToken;
import io.projectenv.core.cli.gson.BaseGsonBuilderFactory;
import io.projectenv.core.cli.integration.assertions.*;
import io.projectenv.core.toolsupport.api.ToolInfo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

abstract class AbstractProjectEnvTest {

    @Test
    void executeProjectEnvShell(@TempDir File projectRoot) throws Exception {
        copyResourceToTarget("git-hook", new File(projectRoot, "hooks"));
        copyResourceToTarget("settings.xml", projectRoot);
        copyResourceToTarget("settings-user.xml", projectRoot);

        File configFile = copyResourceToTarget("project-env.toml", projectRoot);

        executeAndAssertExecution(projectRoot, configFile);
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

    private void executeAndAssertExecution(File projectRoot, File configFile) throws Exception {
        var output = executeProjectEnvShell(
                "--config-file=" + configFile.getAbsolutePath(),
                "--project-root=" + projectRoot.getAbsolutePath()
        );

        var assertions = new SoftAssertions();
        assertions.assertThat(parseOutput(output))
                .containsOnlyKeys("gradle", "jdk", "nodejs", "git", "maven", "generic")
                .hasEntrySatisfying("gradle", new GradleAssertions(assertions))
                .hasEntrySatisfying("jdk", new JdkAssertions(assertions))
                .hasEntrySatisfying("nodejs", new NodeJsAssertions(assertions))
                .hasEntrySatisfying("git", new GitAssertions(assertions))
                .hasEntrySatisfying("maven", new MavenAssertions(assertions))
                .hasEntrySatisfying("generic", new JaxbRiAssertions(assertions));

        assertions.assertAll();
    }

    protected abstract String executeProjectEnvShell(String... params) throws Exception;

    private Map<String, List<ToolInfo>> parseOutput(String output) {
        Type toolInfosType = new TypeToken<Map<String, List<ToolInfo>>>() {
        }.getType();

        return BaseGsonBuilderFactory.createBaseGsonBuilder().create().fromJson(output, toolInfosType);
    }

}
