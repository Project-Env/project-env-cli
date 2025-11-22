package io.projectenv.core.cli.service;

import io.projectenv.core.cli.ProjectEnvException;
import io.projectenv.core.cli.ToolSupportHelper;
import io.projectenv.core.cli.configuration.ProjectEnvConfiguration;
import io.projectenv.core.commons.process.ProcessOutput;
import io.projectenv.core.toolsupport.spi.ToolSupport;
import io.projectenv.core.toolsupport.spi.ToolSupportContext;
import io.projectenv.core.toolsupport.spi.ToolSupportException;
import io.projectenv.core.toolsupport.spi.ToolUpgradeInfo;
import io.projectenv.core.toolsupport.spi.UpgradeScope;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * Service for upgrading tool versions in a ProjectEnv configuration.
 * <p>
 * This service contains the core business logic for tool version upgrades, independent of
 * the presentation layer (CLI, MCP, etc.).
 */
public class ProjectEnvUpgradeService {

    /**
     * Upgrades tool versions and updates the configuration file.
     *
     * @param configuration      the ProjectEnv configuration containing tool definitions
     * @param toolSupportContext the context providing access to tool installation infrastructure
     * @param configFile         the configuration file to update with new versions
     * @param forceScope         optional upgrade scope to force (MAJOR, MINOR, PATCH)
     * @param includeTools       optional list of tool identifiers to include (null or empty means all tools)
     * @return a map of tool identifiers to lists of ToolUpgradeInfo objects representing upgrades
     * @throws ProjectEnvException if tool upgrade fails
     * @throws IOException         if configuration file cannot be read or written
     */
    public Map<String, List<ToolUpgradeInfo>> upgradeToolVersions(
            ProjectEnvConfiguration configuration,
            ToolSupportContext toolSupportContext,
            File configFile,
            UpgradeScope forceScope,
            List<String> includeTools) throws IOException {
        var toolUpgradeInfos = findToolUpgrades(configuration, toolSupportContext, forceScope, includeTools);
        updateProjectEnvConfiguration(configFile, toolUpgradeInfos);
        return toolUpgradeInfos;
    }

    /**
     * Finds available tool upgrades without modifying the configuration file.
     *
     * @param configuration      the ProjectEnv configuration containing tool definitions
     * @param toolSupportContext the context providing access to tool installation infrastructure
     * @param forceScope         optional upgrade scope to force (MAJOR, MINOR, PATCH)
     * @param includeTools       optional list of tool identifiers to include (null or empty means all tools)
     * @return a map of tool identifiers to lists of ToolUpgradeInfo objects representing available upgrades
     * @throws ProjectEnvException if checking for upgrades fails
     */
    public Map<String, List<ToolUpgradeInfo>> findToolUpgrades(
            ProjectEnvConfiguration configuration,
            ToolSupportContext toolSupportContext,
            UpgradeScope forceScope,
            List<String> includeTools) {
        try {
            var toolUpgradeInfos = new LinkedHashMap<String, List<ToolUpgradeInfo>>();
            for (ToolSupport<?> toolSupport : ServiceLoader.load(ToolSupport.class, ToolSupport.class.getClassLoader())) {
                if (!shouldUpgradeTool(toolSupport, includeTools)) {
                    continue;
                }

                List<ToolUpgradeInfo> specificToolUpgradeInfos = findToolUpgrade(toolSupport, configuration, toolSupportContext, forceScope);
                if (!specificToolUpgradeInfos.isEmpty()) {
                    toolUpgradeInfos.put(toolSupport.getToolIdentifier(), specificToolUpgradeInfos);
                }
            }

            return toolUpgradeInfos;
        } catch (ToolSupportException e) {
            throw new ProjectEnvException("Failed to upgrade tools", e);
        }
    }

    private boolean shouldUpgradeTool(ToolSupport<?> toolSupport, List<String> includeTools) {
        return CollectionUtils.isEmpty(includeTools) || includeTools.contains(toolSupport.getToolIdentifier());
    }

    private <T> List<ToolUpgradeInfo> findToolUpgrade(
            ToolSupport<T> toolSupport,
            ProjectEnvConfiguration configuration,
            ToolSupportContext toolSupportContext,
            UpgradeScope forceScope) throws ToolSupportException {
        var toolSupportConfigurationClass = ToolSupportHelper.getToolSupportConfigurationClass(toolSupport);
        var toolConfigurations = configuration.getToolConfigurations(toolSupport.getToolIdentifier(), toolSupportConfigurationClass);
        if (toolConfigurations.isEmpty()) {
            return Collections.emptyList();
        }

        var toolUpgradeInfos = new ArrayList<ToolUpgradeInfo>();
        for (var toolConfiguration : toolConfigurations) {
            if (forceScope != null) {
                toolSupport.upgradeToolVersion(toolConfiguration, forceScope, toolSupportContext).ifPresent(toolUpgradeInfos::add);
            } else {
                toolSupport.upgradeToolVersion(toolConfiguration, toolSupportContext).ifPresent(toolUpgradeInfos::add);
            }
        }

        return toolUpgradeInfos;
    }

    private void updateProjectEnvConfiguration(File configFile, Map<String, List<ToolUpgradeInfo>> toolUpgradeInfos) throws IOException {
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
        ProcessOutput.writeInfoMessage("Upgrading {0} from {1} to {2}...", toolIdentifier, toolUpgradeInfo.getCurrentVersion(), toolUpgradeInfo.getUpgradedVersion());

        String currentVersion = Pattern.quote(toolUpgradeInfo.getCurrentVersion());
        return rawProjectEnvConfiguration
                .replaceFirst("([\\[]{1,2}" + toolIdentifier + "[\\]]{1,2}(\\R|.+)+)(" + currentVersion + ")", "$1" + toolUpgradeInfo.getUpgradedVersion());
    }

}

