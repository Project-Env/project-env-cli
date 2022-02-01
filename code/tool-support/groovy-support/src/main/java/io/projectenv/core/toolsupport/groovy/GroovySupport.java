package io.projectenv.core.toolsupport.groovy;

import io.projectenv.core.toolsupport.commons.commands.*;
import io.projectenv.core.toolsupport.spi.*;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationDetails;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationManagerException;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationStep;
import org.apache.commons.lang3.tuple.Pair;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class GroovySupport implements ToolSupport<GroovyConfiguration> {

    @Override
    public String getToolIdentifier() {
        return "groovy";
    }

    @Override
    public ToolInfo prepareTool(GroovyConfiguration toolConfiguration, ToolSupportContext context) throws ToolSupportException {
        var toolInstallationDetails = installTool(toolConfiguration, context);

        return createProjectEnvToolInfo(toolInstallationDetails);
    }

    private LocalToolInstallationDetails installTool(GroovyConfiguration toolConfiguration, ToolSupportContext context) throws ToolSupportException {
        try {
            var steps = createInstallationSteps(toolConfiguration, context);

            return context.getLocalToolInstallationManager().installOrUpdateTool(getToolIdentifier(), steps);
        } catch (LocalToolInstallationManagerException e) {
            throw new ToolSupportException("failed to install tool", e);
        }
    }

    private List<LocalToolInstallationStep> createInstallationSteps(GroovyConfiguration toolConfiguration, ToolSupportContext context) {
        List<LocalToolInstallationStep> steps = new ArrayList<>();

        steps.add(new ExtractArchiveStep(getSystemSpecificDownloadUri(toolConfiguration)));
        steps.add(new FindBinariesRootStep());
        steps.add(new RegisterPathElementStep("/bin"));
        steps.add(new RegisterMainExecutableStep("groovy"));

        for (var rawPostExtractionCommand : toolConfiguration.getPostExtractionCommands()) {
            steps.add(new ExecuteCommandStep(rawPostExtractionCommand, context.getProjectRoot()));
        }

        return steps;
    }

    protected String getSystemSpecificDownloadUri(GroovyConfiguration toolConfiguration) {
        String version = toolConfiguration.getVersion();

        return MessageFormat.format("https://groovy.jfrog.io/artifactory/dist-release-local/groovy-zips/apache-groovy-binary-{0}.zip", version);
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
