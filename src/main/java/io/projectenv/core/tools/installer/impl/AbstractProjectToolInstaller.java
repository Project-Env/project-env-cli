package io.projectenv.core.tools.installer.impl;

import io.projectenv.core.archive.ArchiveExtractor;
import io.projectenv.core.common.OperatingSystem;
import io.projectenv.core.common.ProcessEnvironmentHelper;
import io.projectenv.core.configuration.DownloadUri;
import io.projectenv.core.configuration.PostExtractionCommand;
import io.projectenv.core.configuration.ToolConfiguration;
import io.projectenv.core.tools.collector.ImmutableToolInfoCollectorContext;
import io.projectenv.core.tools.collector.ToolInfoCollectorContext;
import io.projectenv.core.tools.collector.ToolInfoCollectors;
import io.projectenv.core.tools.info.ToolInfo;
import io.projectenv.core.tools.installer.ProjectToolInstaller;
import io.projectenv.core.tools.installer.ProjectToolInstallerContext;
import io.projectenv.core.tools.installer.ProjectToolInstallerException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.util.Map;

public abstract class AbstractProjectToolInstaller<T extends ToolConfiguration, S extends ToolInfo>
        implements ProjectToolInstaller<T> {

    private final ArchiveExtractor archiveExtractor = new ArchiveExtractor();

    @Override
    public void installTool(T toolConfiguration, ProjectToolInstallerContext context) throws ProjectToolInstallerException {
        try {
            cleanToolInstallationDirectory(context);
            extractToolInstallation(toolConfiguration, context);

            S toolInfo = collectToolInfo(toolConfiguration, context);
            executePostInstallationCommands(toolConfiguration, toolInfo, context);
            executePostInstallationSteps(toolConfiguration, toolInfo, context);
        } catch (Exception e) {
            throw new ProjectToolInstallerException("failed to install tool", e);
        }
    }

    @Override
    public boolean supportsTool(ToolConfiguration toolConfiguration) {
        return getToolConfigurationClass().isAssignableFrom(toolConfiguration.getClass());
    }

    protected abstract Class<T> getToolConfigurationClass();

    private DownloadUri getSystemSpecificDownloadUri(T configuration) {
        return configuration
                .getDownloadUris()
                .stream()
                .filter(downloadUriConfiguration -> {
                    OperatingSystem targetOperatingSystem = downloadUriConfiguration.getTargetOs();

                    return targetOperatingSystem == OperatingSystem.ALL || targetOperatingSystem == OperatingSystem.getCurrentOS();
                })
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("no download URI for OS " + OperatingSystem.getCurrentOS() + " available"));
    }

    private void cleanToolInstallationDirectory(ProjectToolInstallerContext context) throws IOException {
        File projectRoot = context.getToolRoot();

        if (projectRoot.exists()) {
            FileUtils.forceDelete(projectRoot);
        }
        FileUtils.forceMkdir(projectRoot);
    }

    private void extractToolInstallation(T toolInstallationConfiguration, ProjectToolInstallerContext context) throws IOException {
        URI systemSpecificDownloadUri = URI.create(getSystemSpecificDownloadUri(toolInstallationConfiguration).getDownloadUri());

        String archiveName = FilenameUtils.getName(systemSpecificDownloadUri.getPath());
        File tempArchive = Files.createTempFile(toolInstallationConfiguration.getToolName(), archiveName).toFile();

        try (InputStream inputStream = new BufferedInputStream(systemSpecificDownloadUri.toURL().openStream());
             OutputStream outputStream = new FileOutputStream(tempArchive)) {
            IOUtils.copy(inputStream, outputStream);
        }

        archiveExtractor.extractArchive(tempArchive, context.getToolRoot());

        FileUtils.forceDelete(tempArchive);
    }

    private S collectToolInfo(T toolConfiguration, ProjectToolInstallerContext context) {
        ToolInfoCollectorContext collectorContext = ImmutableToolInfoCollectorContext
                .builder()
                .projectRoot(context.getProjectRoot())
                .toolRoot(context.getToolRoot())
                .build();

        return ToolInfoCollectors.collectToolInfo(toolConfiguration, collectorContext);
    }

    private void executePostInstallationCommands(T toolInstallationConfiguration, S toolInfo, ProjectToolInstallerContext context) throws IOException, InterruptedException {
        Map<String, String> processEnvironment = ProcessEnvironmentHelper.createProcessEnvironmentFromToolInfo(toolInfo);

        for (PostExtractionCommand postInstallationCommand : toolInstallationConfiguration.getPostExtractionCommands()) {
            File executable = ProcessEnvironmentHelper.resolveExecutableFromToolInfo(postInstallationCommand.getExecutableName(), toolInfo);
            if (executable == null) {
                throw new IllegalStateException("failed to resolve executable with name " + postInstallationCommand.getExecutableName());
            }

            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.environment().putAll(processEnvironment);
            processBuilder.inheritIO();
            processBuilder.command().add(executable.getAbsolutePath());
            processBuilder.command().addAll(postInstallationCommand.getArguments());
            processBuilder.directory(context.getProjectRoot());
            Process process = processBuilder.start();
            process.waitFor();
        }
    }

    protected void executePostInstallationSteps(T toolInstallationConfiguration, S toolInfo, ProjectToolInstallerContext context) throws IOException {
        // noop
    }

}
