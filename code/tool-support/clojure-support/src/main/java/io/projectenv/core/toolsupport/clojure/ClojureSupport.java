package io.projectenv.core.toolsupport.clojure;

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

public class ClojureSupport extends AbstractUpgradableToolSupport<ClojureConfiguration> {

    @Override
    public String getToolIdentifier() {
        return "clojure";
    }

    @Override
    public Class<ClojureConfiguration> getToolConfigurationClass() {
        return ClojureConfiguration.class;
    }

    @Override
    public ToolInfo prepareTool(ClojureConfiguration toolConfiguration, ToolSupportContext context) throws ToolSupportException {
        var toolInstallationDetails = installTool(toolConfiguration, context);

        return createProjectEnvToolInfo(toolInstallationDetails);
    }

    @Override
    protected String getCurrentVersion(ClojureConfiguration toolConfiguration) {
        return toolConfiguration.getVersion();
    }

    @Override
    protected Set<String> getAllValidVersions(ClojureConfiguration toolConfiguration, ToolSupportContext context) {
        return context.getToolsIndexManager().getClojureVersions();
    }

    private LocalToolInstallationDetails installTool(ClojureConfiguration toolConfiguration, ToolSupportContext context) throws ToolSupportException {
        try {
            var steps = createInstallationSteps(toolConfiguration, context);

            return context.getLocalToolInstallationManager().installOrUpdateTool(getToolIdentifier(), steps);
        } catch (LocalToolInstallationManagerException e) {
            throw new ToolSupportException("failed to install tool", e);
        }
    }

    private List<LocalToolInstallationStep> createInstallationSteps(ClojureConfiguration toolConfiguration, ToolSupportContext context) {
        List<LocalToolInstallationStep> steps = new ArrayList<>();

        steps.add(new ExtractArchiveStep(getSystemSpecificDownloadUri(toolConfiguration, context)));
        steps.add(new FindBinariesRootStep());
        steps.add(new RegisterPathElementStep("/"));

        for (var rawPostExtractionCommand : toolConfiguration.getPostExtractionCommands()) {
            steps.add(new ExecuteCommandStep(rawPostExtractionCommand, context.getProjectRoot()));
        }

        return steps;
    }

    private String getSystemSpecificDownloadUri(ClojureConfiguration toolConfiguration, ToolSupportContext context) {
        return context.getToolsIndexManager().resolveClojureDistributionUrl(toolConfiguration.getVersion());
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
