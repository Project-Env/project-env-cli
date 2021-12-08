package io.projectenv.core.toolsupport.maven;

import io.projectenv.core.toolsupport.commons.commands.*;
import io.projectenv.core.toolsupport.spi.*;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationDetails;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationManagerException;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationStep;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MavenSupport implements ToolSupport<MavenConfiguration> {

    @Override
    public String getToolIdentifier() {
        return "maven";
    }

    @Override
    public ToolInfo prepareTool(MavenConfiguration toolConfiguration, ToolSupportContext context) throws ToolSupportException {
        var toolInstallationDetails = installTool(toolConfiguration, context);

        return createProjectEnvToolInfo(toolConfiguration, toolInstallationDetails, context);
    }

    private LocalToolInstallationDetails installTool(MavenConfiguration toolConfiguration, ToolSupportContext context) throws ToolSupportException {
        try {
            var steps = createInstallationSteps(toolConfiguration, context);

            return context.getLocalToolInstallationManager().installOrUpdateTool(getToolIdentifier(), steps);
        } catch (LocalToolInstallationManagerException e) {
            throw new ToolSupportException("failed to install tool", e);
        }
    }

    private List<LocalToolInstallationStep> createInstallationSteps(MavenConfiguration toolConfiguration, ToolSupportContext context) {
        List<LocalToolInstallationStep> steps = new ArrayList<>();

        steps.add(new ExtractArchiveStep(getSystemSpecificDownloadUri(toolConfiguration)));
        steps.add(new FindBinariesRootStep());
        steps.add(new RegisterPathElementStep("/bin"));
        steps.add(new RegisterMainExecutableStep("mvn"));
        steps.add(new RegisterEnvironmentVariableStep("MAVEN_HOME","/"));

        toolConfiguration
                .getGlobalSettingsFile()
                .ifPresent(s -> steps.add(new OverwriteFileStep(context.getProjectRoot(), s, "conf/settings.xml")));

        for (var rawPostExtractionCommand : toolConfiguration.getPostExtractionCommands()) {
            steps.add(new ExecuteCommandStep(rawPostExtractionCommand, context.getProjectRoot()));
        }

        return steps;
    }

    private String getSystemSpecificDownloadUri(MavenConfiguration toolConfiguration) {
        return MavenDownloadUrlResolver.resolveUrl(toolConfiguration.getVersion());
    }

    private ToolInfo createProjectEnvToolInfo(MavenConfiguration toolConfiguration,
                                              LocalToolInstallationDetails localToolInstallationDetails,
                                              ToolSupportContext context) {

        var builder = ImmutableToolInfo.builder()
                .toolBinariesRoot(localToolInstallationDetails.getBinariesRoot())
                .primaryExecutable(localToolInstallationDetails.getPrimaryExecutable())
                .environmentVariables(localToolInstallationDetails.getEnvironmentVariables())
                .pathElements(localToolInstallationDetails.getPathElements())
                .handledProjectResources(localToolInstallationDetails.getFileOverwrites()
                        .stream().map(Pair::getLeft)
                        .toList());

        toolConfiguration
                .getUserSettingsFile()
                .ifPresent(value -> {
                    var userSettingsFile = new File(context.getProjectRoot(), value);
                    if (userSettingsFile.exists()) {
                        builder.putUnhandledProjectResources("userSettingsFile", userSettingsFile);
                    }
                });

        return builder.build();
    }

}
