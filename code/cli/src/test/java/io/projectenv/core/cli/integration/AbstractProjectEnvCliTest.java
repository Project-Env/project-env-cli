package io.projectenv.core.cli.integration;

import io.projectenv.core.cli.api.ToolInfo;
import io.projectenv.core.cli.api.ToolInfoParser;
import io.projectenv.core.cli.integration.assertions.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static org.assertj.core.api.InstanceOfAssertFactories.list;

abstract class AbstractProjectEnvCliTest {

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
                "--project-root=" + projectRoot.getAbsolutePath(),
                "--debug"
        );

        var result = ToolInfoParser.fromJson(output);

        var assertions = new SoftAssertions();
        assertions.assertThat(result).containsOnlyKeys("gradle", "jdk", "nodejs", "git", "maven", "generic");
        assertions.assertThat(result).extractingByKey("gradle", list(ToolInfo.class))
                .hasSize(1).allSatisfy(new GradleAssertions(assertions));
        assertions.assertThat(result).extractingByKey("jdk", list(ToolInfo.class))
                .hasSize(1).allSatisfy(new JdkAssertions(assertions));
        assertions.assertThat(result).extractingByKey("nodejs", list(ToolInfo.class))
                .hasSize(1).allSatisfy(new NodeJsAssertions(assertions));
        assertions.assertThat(result).extractingByKey("git", list(ToolInfo.class))
                .hasSize(1).allSatisfy(new GitAssertions(assertions));
        assertions.assertThat(result).extractingByKey("maven", list(ToolInfo.class))
                .hasSize(1).allSatisfy(new MavenAssertions(assertions));
        assertions.assertThat(result).extractingByKey("generic", list(ToolInfo.class))
                .hasSize(2).anySatisfy(new JaxbRiAssertions(assertions)).anySatisfy(new MongoDbToolsAssertions(assertions));
        assertions.assertAll();
    }

    protected abstract String executeProjectEnvShell(String... params) throws Exception;

}
