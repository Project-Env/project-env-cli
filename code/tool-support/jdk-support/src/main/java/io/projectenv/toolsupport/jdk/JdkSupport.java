package io.projectenv.toolsupport.jdk;

import io.projectenv.toolsupport.api.ImmutableToolInfo;
import io.projectenv.toolsupport.api.ToolInfo;
import io.projectenv.toolsupport.commons.commands.*;
import io.projectenv.toolsupport.commons.system.OperatingSystem;
import io.projectenv.toolsupport.jdk.download.JdkDownloadUrlResolver;
import io.projectenv.toolsupport.spi.ToolSupport;
import io.projectenv.toolsupport.spi.ToolSupportContext;
import io.projectenv.toolsupport.spi.ToolSupportException;
import io.projectenv.toolsupport.spi.installation.LocalToolInstallationDetails;
import io.projectenv.toolsupport.spi.installation.LocalToolInstallationStep;
import io.projectenv.toolsupport.spi.installation.LocalToolInstallationManagerException;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JdkSupport implements ToolSupport<JdkConfiguration> {

    @Override
    public String getToolIdentifier() {
        return "jdk";
    }

    @Override
    public ToolInfo prepareTool(JdkConfiguration toolConfiguration, ToolSupportContext context) throws ToolSupportException {
        var toolInstallationDetails = installTool(toolConfiguration, context);

        return createProjectEnvToolInfo(toolInstallationDetails);
    }

    private LocalToolInstallationDetails installTool(JdkConfiguration toolConfiguration, ToolSupportContext context) throws ToolSupportException {
        try {
            var steps = createInstallationSteps(toolConfiguration);

            return context.getLocalToolInstallationManager().installOrUpdateTool(getToolIdentifier(), steps);
        } catch (LocalToolInstallationManagerException e) {
            throw new ToolSupportException("failed to install tool", e);
        }
    }

    private List<LocalToolInstallationStep> createInstallationSteps(JdkConfiguration toolConfiguration) {
        List<LocalToolInstallationStep> steps = new ArrayList<>();

        steps.add(new ExtractArchiveStep(getSystemSpecificDownloadUri(toolConfiguration)));
        if (OperatingSystem.getCurrentOperatingSystem() == OperatingSystem.MACOS) {
            steps.add(new SetBinariesRootStep("/Home"));
        }

        steps.add(new RegisterEnvironmentVariableStep("JAVA_HOME", "/"));
        steps.add(new RegisterPathElementStep("/bin"));
        steps.add(new RegisterMainExecutableStep("java"));

        for (var rawPostExtractionCommand : toolConfiguration.getPostExtractionCommands()) {
            steps.add(new ExecuteCommandStep(rawPostExtractionCommand));
        }

        return steps;
    }

    private String getSystemSpecificDownloadUri(JdkConfiguration toolConfiguration) {
        return JdkDownloadUrlResolver.resolveUrl(toolConfiguration);
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
