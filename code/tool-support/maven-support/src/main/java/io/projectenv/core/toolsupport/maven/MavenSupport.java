package io.projectenv.core.toolsupport.maven;

import io.projectenv.core.cli.api.ImmutableToolInfo;
import io.projectenv.core.cli.api.ToolInfo;
import io.projectenv.core.toolsupport.commons.commands.*;
import io.projectenv.core.toolsupport.spi.ToolSupport;
import io.projectenv.core.toolsupport.spi.ToolSupportContext;
import io.projectenv.core.toolsupport.spi.ToolSupportException;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationDetails;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationManagerException;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationStep;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        steps.add(new RegisterPathElementStep("/bin"));
        steps.add(new RegisterMainExecutableStep("mvn"));

        if (StringUtils.isNotEmpty(toolConfiguration.getGlobalSettingsFile())) {
            var globalSettingsFile = new File(context.getProjectRoot(), toolConfiguration.getGlobalSettingsFile());
            if (globalSettingsFile.exists()) {
                steps.add(new OverwriteFileStep(globalSettingsFile, "conf/settings.xml"));
            }
        }

        for (var rawPostExtractionCommand : toolConfiguration.getPostExtractionCommands()) {
            steps.add(new ExecuteCommandStep(rawPostExtractionCommand));
        }

        return steps;
    }

    private String getSystemSpecificDownloadUri(MavenConfiguration toolConfiguration) {
        String version = toolConfiguration.getVersion();

        return MessageFormat.format("https://downloads.apache.org/maven/maven-3/{0}/binaries/apache-maven-{0}-bin.tar.gz", version);
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
                        .collect(Collectors.toList()));

        if (StringUtils.isNotEmpty(toolConfiguration.getUserSettingsFile())) {
            var userSettingsFile = new File(context.getProjectRoot(), toolConfiguration.getUserSettingsFile());
            if (userSettingsFile.exists()) {
                builder.putUnhandledProjectResources("userSettingsFile", userSettingsFile);
            }
        }

        return builder.build();
    }

}
