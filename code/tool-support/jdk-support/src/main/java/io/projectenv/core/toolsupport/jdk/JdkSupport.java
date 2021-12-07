package io.projectenv.core.toolsupport.jdk;

import io.projectenv.core.commons.system.OperatingSystem;
import io.projectenv.core.toolsupport.commons.commands.*;
import io.projectenv.core.toolsupport.jdk.download.JdkDownloadUrlResolver;
import io.projectenv.core.toolsupport.jdk.download.JdkDownloadUrlResolverException;
import io.projectenv.core.toolsupport.jdk.download.JdkDownloadUrlResolverFactory;
import io.projectenv.core.toolsupport.spi.*;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationDetails;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationManagerException;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationStep;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class JdkSupport implements ToolSupport<JdkConfiguration> {

    private final JdkDownloadUrlResolver jdkDownloadUrlResolver = JdkDownloadUrlResolverFactory.createJdkDownloadUrlResolver();

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
            var steps = createInstallationSteps(toolConfiguration, context);

            return context.getLocalToolInstallationManager().installOrUpdateTool(getToolIdentifier(), steps);
        } catch (LocalToolInstallationManagerException | JdkDownloadUrlResolverException e) {
            throw new ToolSupportException("failed to install tool", e);
        }
    }

    private List<LocalToolInstallationStep> createInstallationSteps(JdkConfiguration toolConfiguration, ToolSupportContext context) throws JdkDownloadUrlResolverException {
        List<LocalToolInstallationStep> steps = new ArrayList<>();

        steps.add(new ExtractArchiveStep(getSystemSpecificDownloadUri(toolConfiguration)));
        steps.add(new FindBinariesRootStep());

        if (OperatingSystem.getCurrentOperatingSystem() == OperatingSystem.MACOS) {
            steps.add(new SelectBinariesRootStep("/Home"));
        }

        steps.add(new RegisterEnvironmentVariableStep("JAVA_HOME", "/"));
        steps.add(new RegisterPathElementStep("/bin"));
        steps.add(new RegisterMainExecutableStep("java"));

        for (var rawPostExtractionCommand : toolConfiguration.getPostExtractionCommands()) {
            steps.add(new ExecuteCommandStep(rawPostExtractionCommand, context.getProjectRoot()));
        }

        return steps;
    }

    private String getSystemSpecificDownloadUri(JdkConfiguration toolConfiguration) throws JdkDownloadUrlResolverException {
        return jdkDownloadUrlResolver.resolveUrl(toolConfiguration);
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
