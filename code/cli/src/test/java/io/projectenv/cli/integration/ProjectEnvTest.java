package io.projectenv.cli.integration;


import io.projectenv.cli.ProjectEnv;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.assertj.core.api.Assertions.assertThat;

class ProjectEnvTest extends AbstractProjectEnvTest {

    @Override
    protected String executeProjectEnvShell(String... params) throws Exception {
        return withEnvironmentVariable("USER", "user").execute(() -> {
            var originalStream = System.out;
            try (var outputStream = new ByteArrayOutputStream()) {
                System.setOut(new PrintStream(outputStream));

                executeProjectEnvCli(params);

                return outputStream.toString(StandardCharsets.UTF_8);
            } finally {
                System.setOut(originalStream);
            }
        });
    }

    private void executeProjectEnvCli(String... params) {
        assertThat(ProjectEnv.executeProjectEnvCli(params)).isZero();
    }

}
