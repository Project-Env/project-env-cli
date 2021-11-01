package io.projectenv.core.cli.integration;

import io.projectenv.core.commons.process.ProcessHelper;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectEnvCliIT extends AbstractProjectEnvCliTest {

    @Override
    protected String executeProjectEnvShell(String... params) throws Exception {
        var commands = new ArrayList<String>();
        commands.add("./target/project-env-cli");
        commands.addAll(Arrays.asList(params));

        var processBuilder = new ProcessBuilder(commands);
        processBuilder.environment().put("USER", "user");

        var processResult = ProcessHelper.executeProcess(processBuilder, true);
        assertThat(processResult.getExitCode()).describedAs("process exit code").isZero();

        return processResult.getStdOutput().orElse(null);
    }

}
