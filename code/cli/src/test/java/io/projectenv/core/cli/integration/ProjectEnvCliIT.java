package io.projectenv.core.cli.integration;

import io.projectenv.core.commons.process.ProcessHelper;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectEnvCliIT extends AbstractProjectEnvCliTest {

    @Override
    protected String executeProjectEnvShell(String... params) throws Exception {
        var commands = new ArrayList<String>();
        commands.add("./target/project-env-cli");
        commands.addAll(Arrays.asList(params));

        var processBuilder = new ProcessBuilder(commands);
        processBuilder.environment().put("USERNAME", "user");
        processBuilder.environment().put("USER", "user");

        var process = processBuilder.start();
        ProcessHelper.bindErrOutput(process);

        var terminated = process.waitFor(5, TimeUnit.MINUTES);
        var output = process.getInputStream().readAllBytes();
        if (!terminated) {
            process.destroy();
        }
        assertThat(process.exitValue()).describedAs("process exit code").isZero();

        return StringUtils.toEncodedString(output, StandardCharsets.UTF_8);
    }

}
