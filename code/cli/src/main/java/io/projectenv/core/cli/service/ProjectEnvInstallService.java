package io.projectenv.core.cli.service;

import io.projectenv.core.cli.ProjectEnvException;
import io.projectenv.core.cli.ToolSupportHelper;
import io.projectenv.core.cli.configuration.ProjectEnvConfiguration;
import io.projectenv.core.commons.process.ProcessOutput;
import io.projectenv.core.toolsupport.spi.ToolInfo;
import io.projectenv.core.toolsupport.spi.ToolSupport;
import io.projectenv.core.toolsupport.spi.ToolSupportContext;
import io.projectenv.core.toolsupport.spi.ToolSupportException;

import java.util.*;

/**
 * Service for installing or updating tools configured in a ProjectEnv configuration.
 * <p>
 * This service contains the core business logic for tool installation, independent of
 * the presentation layer (CLI, MCP, etc.).
 */
public class ProjectEnvInstallService {

    /**
     * Installs or updates all tools configured in the project environment configuration.
     *
     * @param configuration      the ProjectEnv configuration containing tool definitions
     * @param toolSupportContext the context providing access to tool installation infrastructure
     * @return a map of tool identifiers to lists of ToolInfo objects representing installed tools
     * @throws ProjectEnvException if tool installation fails
     */
    public Map<String, List<ToolInfo>> installOrUpdateTools(
            ProjectEnvConfiguration configuration,
            ToolSupportContext toolSupportContext) {
        try {
            var toolInstallationInfos = new LinkedHashMap<String, List<ToolInfo>>();
            for (ToolSupport<?> toolSupport : ServiceLoader.load(ToolSupport.class, ToolSupport.class.getClassLoader())) {
                List<ToolInfo> toolInfos = installOrUpdateTool(toolSupport, configuration, toolSupportContext);
                if (!toolInfos.isEmpty()) {
                    toolInstallationInfos.put(toolSupport.getToolIdentifier(), toolInfos);
                }
            }

            return toolInstallationInfos;
        } catch (ToolSupportException e) {
            throw new ProjectEnvException("Failed to install tools", e);
        }
    }

    private <T> List<ToolInfo> installOrUpdateTool(
            ToolSupport<T> toolSupport,
            ProjectEnvConfiguration configuration,
            ToolSupportContext toolSupportContext) throws ToolSupportException {
        var toolSupportConfigurationClass = ToolSupportHelper.getToolSupportConfigurationClass(toolSupport);
        var toolConfigurations = configuration.getToolConfigurations(toolSupport.getToolIdentifier(), toolSupportConfigurationClass);
        if (toolConfigurations.isEmpty()) {
            return Collections.emptyList();
        }

        var toolInfos = new ArrayList<ToolInfo>();
        for (var toolConfiguration : toolConfigurations) {
            if (toolSupport.isAvailable(toolConfiguration)) {
                ProcessOutput.writeInfoMessage("Installing {0}...", toolSupport.getDescription(toolConfiguration));
                toolInfos.add(toolSupport.prepareTool(toolConfiguration, toolSupportContext));
            } else {
                ProcessOutput.writeInfoMessage("{0} is not available, skipping installation", toolSupport.getToolIdentifier());
            }
        }

        return toolInfos;
    }

}

