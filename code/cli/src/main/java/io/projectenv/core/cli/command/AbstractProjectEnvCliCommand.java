package io.projectenv.core.cli.command;

import io.projectenv.core.cli.configuration.ProjectEnvConfiguration;
import io.projectenv.core.cli.configuration.toml.TomlConfigurationFactory;
import io.projectenv.core.cli.http.DefaultHttpClientProvider;
import io.projectenv.core.cli.index.DefaultToolsIndexManager;
import io.projectenv.core.cli.installer.DefaultLocalToolInstallationManager;
import io.projectenv.core.commons.process.ProcessOutput;
import io.projectenv.core.toolsupport.spi.ImmutableToolSupportContext;
import io.projectenv.core.toolsupport.spi.ToolSupportContext;
import org.apache.commons.lang3.Strings;
import org.apache.commons.lang3.exception.ExceptionUtils;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

public abstract class AbstractProjectEnvCliCommand implements Callable<Integer> {

    private static final String DEFAULT_PROJECT_ENV_TOML = "project-env.toml";

    @Option(names = {"--project-root"}, defaultValue = ".")
    protected String projectRoot;

    @Option(names = {"--config-file"}, defaultValue = DEFAULT_PROJECT_ENV_TOML)
    protected String configFile;

    @Option(names = {"--debug"})
    protected boolean debug;

    @Spec
    private CommandSpec spec;

    @Override
    public Integer call() {
        try {
            if (debug) {
                ProcessOutput.activateDebugMode();
            }

            var configuration = readProjectEnvConfiguration();
            var toolSupportContext = createToolSupportContext(configuration);

            callInternal(configuration, toolSupportContext);

            return CommandLine.ExitCode.OK;
        } catch (Exception e) {
            var rootCauseMessage = ExceptionUtils.getRootCauseMessage(e);

            ProcessOutput.writeInfoMessage("Failed to " + spec.commandLine().getCommandName() + " tools: {0}", rootCauseMessage);
            ProcessOutput.writeDebugMessage(e);

            return CommandLine.ExitCode.SOFTWARE;
        }
    }


    private ProjectEnvConfiguration readProjectEnvConfiguration() throws IOException {
        return TomlConfigurationFactory.fromFile(resolveConfigFile());
    }

    protected File resolveConfigFile() {
        if (Strings.CS.equals(configFile, DEFAULT_PROJECT_ENV_TOML)) {
            return new File(projectRoot, DEFAULT_PROJECT_ENV_TOML);
        } else {
            return new File(configFile);
        }
    }

    private ToolSupportContext createToolSupportContext(ProjectEnvConfiguration configuration) throws IOException {
        var projectRoot = new File(this.projectRoot);
        var toolsDirectory = new File(projectRoot, configuration.getToolsDirectory());
        if (!toolsDirectory.getCanonicalPath().startsWith(projectRoot.getCanonicalPath())) {
            throw new IllegalArgumentException("Tools root must be located in project root");
        }

        var localToolInstallationManager = new DefaultLocalToolInstallationManager(toolsDirectory);
        var httpClientProvider = new DefaultHttpClientProvider();
        var toolsIndexManager = new DefaultToolsIndexManager(toolsDirectory, httpClientProvider);

        return ImmutableToolSupportContext.builder()
                .projectRoot(projectRoot)
                .localToolInstallationManager(localToolInstallationManager)
                .toolsIndexManager(toolsIndexManager)
                .httpClientProvider(httpClientProvider)
                .build();
    }

    protected abstract void callInternal(ProjectEnvConfiguration configuration, ToolSupportContext context) throws Exception;

}
