package io.projectenv.core.toolsupport.nodejs;

import io.projectenv.core.cli.api.ImmutableToolInfo;
import io.projectenv.core.cli.api.ToolInfo;
import io.projectenv.core.toolsupport.commons.commands.ExecuteCommandStep;
import io.projectenv.core.toolsupport.commons.commands.ExtractArchiveStep;
import io.projectenv.core.toolsupport.commons.commands.RegisterMainExecutableStep;
import io.projectenv.core.toolsupport.commons.commands.RegisterPathElementStep;
import io.projectenv.core.commons.download.DownloadUrlSubstitutorFactory;
import io.projectenv.core.commons.download.ImmutableDownloadUrlDictionary;
import io.projectenv.core.commons.system.CPUArchitecture;
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
import java.util.Map;
import java.util.stream.Collectors;

public class NodeJsSupport implements ToolSupport<NodeJsConfiguration> {

    @Override
    public String getToolIdentifier() {
        return "nodejs";
    }

    @Override
    public ToolInfo prepareTool(NodeJsConfiguration toolConfiguration, ToolSupportContext context) throws ToolSupportException {
        var toolInstallationDetails = installTool(toolConfiguration, context);

        return createProjectEnvToolInfo(toolInstallationDetails);
    }

    private LocalToolInstallationDetails installTool(NodeJsConfiguration toolConfiguration, ToolSupportContext context) throws ToolSupportException {
        try {
            var steps = createInstallationSteps(toolConfiguration);

            return context.getLocalToolInstallationManager().installOrUpdateTool(getToolIdentifier(), steps);
        } catch (LocalToolInstallationManagerException e) {
            throw new ToolSupportException("failed to install tool", e);
        }
    }

    private List<LocalToolInstallationStep> createInstallationSteps(NodeJsConfiguration toolConfiguration) {
        List<LocalToolInstallationStep> steps = new ArrayList<>();

        steps.add(new ExtractArchiveStep(getSystemSpecificDownloadUri(toolConfiguration)));

        if (OperatingSystem.getCurrentOperatingSystem() == OperatingSystem.WINDOWS) {
            steps.add(new RegisterPathElementStep("/"));
        } else {
            steps.add(new RegisterPathElementStep("/bin"));
        }

        steps.add(new RegisterMainExecutableStep("node"));

        for (var rawPostExtractionCommand : toolConfiguration.getPostExtractionCommands()) {
            steps.add(new ExecuteCommandStep(rawPostExtractionCommand));
        }

        return steps;
    }

    private String getSystemSpecificDownloadUri(NodeJsConfiguration toolConfiguration) {
        var dictionary = ImmutableDownloadUrlDictionary.builder()
                .putParameters("VERSION", toolConfiguration.getVersion())
                .putOperatingSystemSpecificParameters("OS", Map.of(
                        OperatingSystem.MACOS, "darwin",
                        OperatingSystem.LINUX, "linux",
                        OperatingSystem.WINDOWS, "win"
                ))
                .putOperatingSystemSpecificParameters("FILE_EXT", Map.of(
                        OperatingSystem.MACOS, "tar.xz",
                        OperatingSystem.LINUX, "tar.xz",
                        OperatingSystem.WINDOWS, "zip"
                ))
                .putCPUArchitectureSpecificParameters("CPU_ARCH", Map.of(
                        CPUArchitecture.X64, "x64"
                ))
                .build();

        return DownloadUrlSubstitutorFactory
                .createDownloadUrlVariableSubstitutor(dictionary)
                .replace("https://nodejs.org/dist/v${VERSION}/node-v${VERSION}-${OS}-${CPU_ARCH}.${FILE_EXT}");
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
