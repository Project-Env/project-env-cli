package io.projectenv.core.cli;

import io.projectenv.core.cli.command.ProjectEnvInstallCommand;
import io.projectenv.core.cli.command.ProjectEnvMcpCommand;
import io.projectenv.core.cli.command.ProjectEnvUpgradeCommand;
import org.apache.commons.lang3.ArrayUtils;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "project-env-cli", subcommands = {ProjectEnvInstallCommand.class, ProjectEnvUpgradeCommand.class, ProjectEnvMcpCommand.class})
public final class ProjectEnvCli {

    public static void main(String[] args) {
        System.exit(executeProjectEnvCli(args));
    }

    public static int executeProjectEnvCli(String[] args) {
        var commandLine = new CommandLine(new ProjectEnvCli()).setCaseInsensitiveEnumValuesAllowed(true);

        commandLine.setUnmatchedArgumentsAllowed(true);
        var result = commandLine.parseArgs(args);
        if (!result.hasSubcommand()) {
            args = ArrayUtils.addFirst(args, "install");
        }
        commandLine.setUnmatchedArgumentsAllowed(false);

        return commandLine.execute(args);
    }

}
