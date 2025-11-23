package io.projectenv.core.cli.command;

import io.projectenv.core.cli.configuration.ProjectEnvConfiguration;
import io.projectenv.core.cli.parser.ToolUpgradeInfoParser;
import io.projectenv.core.cli.service.ProjectEnvUpgradeService;
import io.projectenv.core.commons.process.ProcessOutput;
import io.projectenv.core.toolsupport.spi.ToolUpgradeInfo;
import io.projectenv.core.toolsupport.spi.ToolSupportContext;
import io.projectenv.core.toolsupport.spi.UpgradeScope;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Command(name = "upgrade")
public class ProjectEnvUpgradeCommand extends AbstractProjectEnvCliCommand {

    @Option(names = {"--force-scope"})
    protected UpgradeScope scope;

    @Option(names = {"--include-tools"}, split = ",")
    protected List<String> includeTools;

    @Override
    protected void callInternal(ProjectEnvConfiguration configuration, ToolSupportContext toolSupportContext) throws IOException {
        var service = new ProjectEnvUpgradeService();
        var toolUpgradeInfos = service.upgradeToolVersions(
                configuration,
                toolSupportContext,
                resolveConfigFile(),
                scope,
                includeTools
        );
        writeOutput(toolUpgradeInfos);
    }


    private void writeOutput(Map<String, List<ToolUpgradeInfo>> toolUpgradeInfos) {
        ProcessOutput.writeResult(ToolUpgradeInfoParser.toJson(toolUpgradeInfos));
    }

}
