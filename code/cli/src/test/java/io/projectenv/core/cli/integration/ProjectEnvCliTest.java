package io.projectenv.core.cli.integration;

import io.projectenv.core.cli.ProjectEnvCli;
import io.projectenv.core.commons.system.EnvironmentVariablesMock;
import org.assertj.core.api.Assertions;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

class ProjectEnvCliTest extends AbstractProjectEnvCliTest {

    @Override
    protected String executeProjectEnvShell(String... params) throws Exception {
        try (var environmentVariablesMock = EnvironmentVariablesMock.withEnvOverlay(Map.of("USER", "user"))) {
            var originalStream = System.out;
            try (var outputStream = new ByteArrayOutputStream()) {
                System.setOut(new PrintStream(outputStream));

                executeProjectEnvCli(params);

                return outputStream.toString(StandardCharsets.UTF_8);
            } finally {
                System.setOut(originalStream);
            }
        }
    }

    private void executeProjectEnvCli(String... params) {
        Assertions.assertThat(ProjectEnvCli.executeProjectEnvCli(params)).isZero();
    }

}
