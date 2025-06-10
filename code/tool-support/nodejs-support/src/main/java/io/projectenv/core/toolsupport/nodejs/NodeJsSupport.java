package io.projectenv.core.toolsupport.nodejs;

import io.projectenv.core.commons.system.OperatingSystem;
import io.projectenv.core.toolsupport.commons.AbstractUpgradableToolSupport;
import io.projectenv.core.toolsupport.commons.commands.*;
import io.projectenv.core.toolsupport.spi.ImmutableToolInfo;
import io.projectenv.core.toolsupport.spi.ToolInfo;
import io.projectenv.core.toolsupport.spi.ToolSupportContext;
import io.projectenv.core.toolsupport.spi.ToolSupportException;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationDetails;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationManagerException;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationStep;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class NodeJsSupport extends AbstractUpgradableToolSupport<NodeJsConfiguration> {

    @Override
    public String getToolIdentifier() {
        return "nodejs";
    }

    @Override
    public Class<NodeJsConfiguration> getToolConfigurationClass() {
        return NodeJsConfiguration.class;
    }

    @Override
    public ToolInfo prepareTool(NodeJsConfiguration toolConfiguration, ToolSupportContext context) throws ToolSupportException {
        var toolInstallationDetails = installTool(toolConfiguration, context);

        return createProjectEnvToolInfo(toolInstallationDetails);
    }

    @Override
    protected String getCurrentVersion(NodeJsConfiguration toolConfiguration) {
        return toolConfiguration.getVersion();
    }

    @Override
    protected Set<String> getAllValidVersions(NodeJsConfiguration toolConfiguration, ToolSupportContext context) {
        return context.getToolsIndexManager().getNodeJsVersions();
    }

    private LocalToolInstallationDetails installTool(NodeJsConfiguration toolConfiguration, ToolSupportContext context) throws ToolSupportException {
        try {
            var steps = createInstallationSteps(toolConfiguration, context);

            return context.getLocalToolInstallationManager().installOrUpdateTool(getToolIdentifier(), steps);
        } catch (LocalToolInstallationManagerException e) {
            throw new ToolSupportException("failed to install tool", e);
        }
    }

    private List<LocalToolInstallationStep> createInstallationSteps(NodeJsConfiguration toolConfiguration, ToolSupportContext context) {
        List<LocalToolInstallationStep> steps = new ArrayList<>();

        steps.add(new ExtractArchiveStep(getSystemSpecificDownloadUri(toolConfiguration, context), context.getHttpClientProvider()));
        steps.add(new FindBinariesRootStep());

        if (OperatingSystem.getCurrentOperatingSystem() == OperatingSystem.WINDOWS) {
            steps.add(new RegisterPathElementStep("/"));
        } else {
            steps.add(new RegisterPathElementStep("/bin"));
        }

        steps.add(new RegisterMainExecutableStep("node"));

        for (var rawPostExtractionCommand : toolConfiguration.getPostExtractionCommands()) {
            steps.add(new ExecuteCommandStep(rawPostExtractionCommand, context.getProjectRoot()));
        }

        return steps;
    }

    private String getSystemSpecificDownloadUri(NodeJsConfiguration toolConfiguration, ToolSupportContext context) {
        return context.getToolsIndexManager().resolveNodeJsDistributionUrl(toolConfiguration.getVersion());
    }

    private ToolInfo createProjectEnvToolInfo(LocalToolInstallationDetails localToolInstallationDetails) {
        return ImmutableToolInfo.builder()
                .toolBinariesRoot(localToolInstallationDetails.getBinariesRoot())
                .primaryExecutable(localToolInstallationDetails.getPrimaryExecutable())
                .environmentVariables(localToolInstallationDetails.getEnvironmentVariables())
                .pathElements(localToolInstallationDetails.getPathElements())
                .handledProjectResources(localToolInstallationDetails.getFileOverwrites()
                        .stream().map(Pair::getLeft)
                        .toList())
                .build();
    }

}
