package io.projectenv.core.cli.integration;

import io.projectenv.core.cli.integration.assertions.*;
import io.projectenv.core.cli.parser.ToolInfoParser;
import io.projectenv.core.cli.parser.ToolUpgradeInfoParser;
import io.projectenv.core.toolsupport.spi.ToolInfo;
import io.projectenv.core.toolsupport.spi.ToolUpgradeInfo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.list;

abstract class AbstractProjectEnvCliTest {

    @Test
    void executeProjectEnvCliInstall(@TempDir File projectRoot) throws Exception {
        copyResourceToTarget("git-hook", new File(projectRoot, "hooks"));
        copyResourceToTarget("settings.xml", projectRoot);
        copyResourceToTarget("settings-user.xml", projectRoot);

        File configFile = copyResourceToTarget("project-env.toml", projectRoot);

        var output = executeProjectEnvShell(
                "--config-file=" + configFile.getAbsolutePath(),
                "--project-root=" + projectRoot.getAbsolutePath(),
                "--debug"
        );

        var result = ToolInfoParser.fromJson(output);

        var assertions = new SoftAssertions();
        assertions.assertThat(result).containsOnlyKeys("gradle", "jdk", "nodejs", "git", "maven", "clojure", "generic");
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
        assertions.assertThat(result).extractingByKey("clojure", list(ToolInfo.class))
                .hasSize(1).allSatisfy(new ClojureAssertions(assertions));
        assertions.assertThat(result).extractingByKey("generic", list(ToolInfo.class))
                .hasSize(3)
                .anySatisfy(new JaxbRiAssertions(assertions))
                .anySatisfy(new MongoDbToolsAssertions(assertions))
                .anySatisfy(new MongoDbToolsAssertions(assertions))
                .anySatisfy(new TerraformToolsAssertions(assertions));
        assertions.assertAll();
    }

    @Test
    void executeProjectEnvCliUpgrade(@TempDir File projectRoot) throws Exception {
        copyResourceToTarget("git-hook", new File(projectRoot, "hooks"));
        copyResourceToTarget("settings.xml", projectRoot);
        copyResourceToTarget("settings-user.xml", projectRoot);

        File configFile = copyResourceToTarget("project-env.toml", projectRoot);

        var output = executeProjectEnvShell(
                "upgrade",
                "--config-file=" + configFile.getAbsolutePath(),
                "--project-root=" + projectRoot.getAbsolutePath(),
                "--debug"
        );

        var result = ToolUpgradeInfoParser.fromJson(output);

        var assertions = new SoftAssertions();
        assertions.assertThat(result).containsOnlyKeys("gradle", "nodejs");
        assertions.assertThat(result).extractingByKey("gradle", list(ToolUpgradeInfo.class))
                .hasSize(1).allSatisfy((toolUpgradeInfo) -> {
                    assertions.assertThat(toolUpgradeInfo.getCurrentVersion()).isEqualTo("^6.7.1");
                    assertions.assertThat(toolUpgradeInfo.getUpgradedVersion()).isEqualTo("^6.9.4");
                });
        assertions.assertThat(result).extractingByKey("nodejs", list(ToolUpgradeInfo.class))
                .hasSize(1).allSatisfy((toolUpgradeInfo) -> {
                    assertions.assertThat(toolUpgradeInfo.getCurrentVersion()).isEqualTo("~14.15.3");
                    assertions.assertThat(toolUpgradeInfo.getUpgradedVersion()).isEqualTo("~14.15.5");
                });
        assertions.assertAll();
    }

    @Test
    void executeProjectEnvCliInstallWithShellScriptOutput(@TempDir File projectRoot)  throws Exception{
        copyResourceToTarget("git-hook", new File(projectRoot, "hooks"));
        copyResourceToTarget("settings.xml", projectRoot);
        copyResourceToTarget("settings-user.xml", projectRoot);

        File configFile = copyResourceToTarget("project-env.toml", projectRoot);

        var output = executeProjectEnvShell(
                "install",
                "--config-file=" + configFile.getAbsolutePath(),
                "--project-root=" + projectRoot.getAbsolutePath(),
                "--output-template=sh"
        );

        assertThat(output)
                .contains("export MAVEN_HOME")
                .contains("export JAVA_HOME")
                .contains("export JAXB_HOME")
                .contains("alias mvn")
                .contains("export PATH");
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

    protected abstract String executeProjectEnvShell(String... params) throws Exception;

}
