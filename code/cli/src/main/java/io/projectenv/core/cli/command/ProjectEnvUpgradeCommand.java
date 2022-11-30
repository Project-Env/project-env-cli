package io.projectenv.core.cli.command;

import io.projectenv.core.cli.ProjectEnvException;
import io.projectenv.core.cli.ToolSupportHelper;
import io.projectenv.core.cli.configuration.ProjectEnvConfiguration;
import io.projectenv.core.cli.parser.ToolUpgradeInfoParser;
import io.projectenv.core.commons.process.ProcessOutput;
import io.projectenv.core.toolsupport.spi.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

@Command(name = "upgrade")
public class ProjectEnvUpgradeCommand extends AbstractProjectEnvCliCommand {

    @Option(names = {"--force-scope"})
    protected UpgradeScope scope;

    @Option(names = {"--include-tools"}, split = ",")
    protected List<String> includeTools;

    @Override
    protected void callInternal(ProjectEnvConfiguration configuration, ToolSupportContext toolSupportContext) throws Exception {
        var toolUpgradeInfos = upgradeToolVersions(configuration, toolSupportContext);

        updateProjectEnvConfiguration(toolUpgradeInfos);
        writeOutput(toolUpgradeInfos);
    }

    private Map<String, List<ToolUpgradeInfo>> upgradeToolVersions(ProjectEnvConfiguration configuration, ToolSupportContext toolSupportContext) {
        try {
            var toolUpgradeInfos = new LinkedHashMap<String, List<ToolUpgradeInfo>>();
            for (ToolSupport<?> toolSupport : ServiceLoader.load(ToolSupport.class, ToolSupport.class.getClassLoader())) {
                if (!shouldUpgradeTool(toolSupport)) {
                    continue;
                }

                List<ToolUpgradeInfo> specificToolUpgradeInfos = upgradeToolVersion(toolSupport, configuration, toolSupportContext);
                if (!specificToolUpgradeInfos.isEmpty()) {
                    toolUpgradeInfos.put(toolSupport.getToolIdentifier(), specificToolUpgradeInfos);
                }
            }

            return toolUpgradeInfos;
        } catch (ToolSupportException e) {
            throw new ProjectEnvException("failed to upgrade tools", e);
        }
    }

    private boolean shouldUpgradeTool(ToolSupport<?> toolSupport) {
        return CollectionUtils.isEmpty(includeTools) || includeTools.contains(toolSupport.getToolIdentifier());
    }

    private <T> List<ToolUpgradeInfo> upgradeToolVersion(ToolSupport<T> toolSupport, ProjectEnvConfiguration configuration, ToolSupportContext toolSupportContext) throws ToolSupportException {
        var toolSupportConfigurationClass = ToolSupportHelper.getToolSupportConfigurationClass(toolSupport);
        var toolConfigurations = configuration.getToolConfigurations(toolSupport.getToolIdentifier(), toolSupportConfigurationClass);
        if (toolConfigurations.isEmpty()) {
            return Collections.emptyList();
        }

        var toolUpgradeInfos = new ArrayList<ToolUpgradeInfo>();
        for (var toolConfiguration : toolConfigurations) {
            if (scope != null) {
                toolSupport.upgradeToolVersion(toolConfiguration, scope, toolSupportContext).ifPresent(toolUpgradeInfos::add);
            } else {
                toolSupport.upgradeToolVersion(toolConfiguration, toolSupportContext).ifPresent(toolUpgradeInfos::add);
            }
        }

        return toolUpgradeInfos;
    }

    private void updateProjectEnvConfiguration(Map<String, List<ToolUpgradeInfo>> toolUpgradeInfos) throws IOException {
        String rawProjectEnvConfiguration = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);
        for (Entry<String, List<ToolUpgradeInfo>> entry : toolUpgradeInfos.entrySet()) {
            rawProjectEnvConfiguration = applyToolUpgrade(rawProjectEnvConfiguration, entry);
        }

        FileUtils.write(configFile, rawProjectEnvConfiguration, StandardCharsets.UTF_8);
    }

    private String applyToolUpgrade(String rawProjectEnvConfiguration, Entry<String, List<ToolUpgradeInfo>> entry) {
        String toolIdentifier = entry.getKey();
        for (ToolUpgradeInfo toolUpgradeInfo : entry.getValue()) {
            rawProjectEnvConfiguration = applyToolUpgrade(rawProjectEnvConfiguration, toolIdentifier, toolUpgradeInfo);
        }

        return rawProjectEnvConfiguration;
    }

    private String applyToolUpgrade(String rawProjectEnvConfiguration, String toolIdentifier, ToolUpgradeInfo toolUpgradeInfo) {
        ProcessOutput.writeInfoMessage("upgrading {0} from {1} to {2}...", toolIdentifier, toolUpgradeInfo.getCurrentVersion(), toolUpgradeInfo.getUpgradedVersion());

        String currentVersion = Pattern.quote(toolUpgradeInfo.getCurrentVersion());
        return rawProjectEnvConfiguration
                .replaceFirst("([\\[]{1,2}" + toolIdentifier + "[\\]]{1,2}(\\R|.+)+)(" + currentVersion + ")", "$1" + toolUpgradeInfo.getUpgradedVersion());
    }

    private void writeOutput(Map<String, List<ToolUpgradeInfo>> toolUpgradeInfos) {
        ProcessOutput.writeResult(ToolUpgradeInfoParser.toJson(toolUpgradeInfos));
    }

}
