package io.projectenv.core.toolsupport.nodejs;

import io.projectenv.core.cli.api.ImmutableToolInfo;
import io.projectenv.core.cli.api.ToolInfo;
import io.projectenv.core.toolsupport.commons.commands.*;
import io.projectenv.core.commons.system.OperatingSystem;
import io.projectenv.core.toolsupport.spi.ToolSupport;
import io.projectenv.core.toolsupport.spi.ToolSupportContext;
import io.projectenv.core.toolsupport.spi.ToolSupportException;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationDetails;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationManagerException;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationStep;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GenericToolSupport implements ToolSupport<GenericToolConfiguration> {

    @Override
    public String getToolIdentifier() {
        return "generic";
    }

    @Override
    public ToolInfo prepareTool(GenericToolConfiguration toolConfiguration, ToolSupportContext context) throws ToolSupportException {
        var toolInstallationDetails = installTool(toolConfiguration, context);

        return createProjectEnvToolInfo(toolInstallationDetails);
    }

    private LocalToolInstallationDetails installTool(GenericToolConfiguration toolConfiguration, ToolSupportContext context) throws ToolSupportException {
        try {
            var steps = createInstallationSteps(toolConfiguration);

            return context.getLocalToolInstallationManager().installOrUpdateTool(getToolIdentifier(), steps);
        } catch (LocalToolInstallationManagerException e) {
            throw new ToolSupportException("failed to install tool", e);
        }
    }

    private List<LocalToolInstallationStep> createInstallationSteps(GenericToolConfiguration toolConfiguration) {
        List<LocalToolInstallationStep> steps = new ArrayList<>();

        steps.add(new ExtractArchiveStep(getSystemSpecificDownloadUri(toolConfiguration)));

        for (var pathElement : toolConfiguration.getPathElements()) {
            steps.add(new RegisterPathElementStep(pathElement));
        }

        for (var entry : toolConfiguration.getEnvironmentVariables().entrySet()) {
            steps.add(new RegisterEnvironmentVariableStep(entry.getKey(), entry.getValue()));
        }

        toolConfiguration.getPrimaryExecutable().ifPresent(s -> steps.add(new RegisterMainExecutableStep(s)));

        for (var rawPostExtractionCommand : toolConfiguration.getPostExtractionCommands()) {
            steps.add(new ExecuteCommandStep(rawPostExtractionCommand));
        }

        return steps;
    }

    private String getSystemSpecificDownloadUri(GenericToolConfiguration toolConfiguration) {
        return toolConfiguration.getDownloadUrls()
                .stream()
                .filter(downloadUrlConfiguration -> downloadUrlConfiguration.getTargetOS() == OperatingSystem.getCurrentOperatingSystem())
                .findFirst()
                .map(GenericToolConfiguration.DownloadUrlConfiguration::getDownloadUrl)
                .or(toolConfiguration::getDownloadUrl)
                .orElseThrow();
    }

    private ToolInfo createProjectEnvToolInfo(LocalToolInstallationDetails localToolInstallationDetails) {
        return ImmutableToolInfo.builder()
                .toolBinariesRoot(localToolInstallationDetails.getBinariesRoot())
                .primaryExecutable(localToolInstallationDetails.getPrimaryExecutable())
                .environmentVariables(localToolInstallationDetails.getEnvironmentVariables())
                .pathElements(localToolInstallationDetails.getPathElements())
                .handledProjectResources(localToolInstallationDetails.getFileOverwrites()
                        .stream().map(Pair::getLeft)
                        .collect(Collectors.toList()))
                .build();
    }

}
