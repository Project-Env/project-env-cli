package io.projectenv.core.tools.service.installer.impl;

import io.projectenv.core.archive.ArchiveExtractor;
import io.projectenv.core.common.OperatingSystem;
import io.projectenv.core.common.ProcessEnvironmentHelper;
import io.projectenv.core.configuration.DownloadUri;
import io.projectenv.core.configuration.PostExtractionCommand;
import io.projectenv.core.configuration.SimpleToolConfiguration;
import io.projectenv.core.configuration.ToolConfiguration;
import io.projectenv.core.tools.info.SimpleToolInfo;
import io.projectenv.core.tools.service.ToolSpecificServiceContext;
import io.projectenv.core.tools.service.ToolSpecificServices;
import io.projectenv.core.tools.service.installer.ToolInstaller;
import io.projectenv.core.tools.service.installer.ToolInstallerException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.util.Map;

public class SimpleToolInstaller<T extends SimpleToolConfiguration> implements ToolInstaller<T> {

    private final ArchiveExtractor archiveExtractor = new ArchiveExtractor();

    @Override
    public void installTool(T toolConfiguration, ToolSpecificServiceContext context) throws ToolInstallerException {
        try {
            cleanToolInstallationDirectory(context);
            extractToolInstallation(toolConfiguration, context);
            executePostInstallationCommands(toolConfiguration, context);
        } catch (Exception e) {
            throw new ToolInstallerException("failed to install tool", e);
        }
    }

    @Override
    public boolean supportsTool(ToolConfiguration toolConfiguration) {
        return toolConfiguration instanceof SimpleToolConfiguration;
    }

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

    private void cleanToolInstallationDirectory(ToolSpecificServiceContext context) throws IOException {
        File projectRoot = context.getToolRoot();

        if (projectRoot.exists()) {
            FileUtils.forceDelete(projectRoot);
        }
        FileUtils.forceMkdir(projectRoot);
    }

    private void extractToolInstallation(T toolInstallationConfiguration, ToolSpecificServiceContext context) throws IOException {
        URI systemSpecificDownloadUri = URI.create(getSystemSpecificDownloadUri(toolInstallationConfiguration).getDownloadUri());

        String archiveName = FilenameUtils.getName(systemSpecificDownloadUri.getPath());
        File tempArchive = Files.createTempFile(null, archiveName).toFile();

        try (InputStream inputStream = new BufferedInputStream(systemSpecificDownloadUri.toURL().openStream());
             OutputStream outputStream = new FileOutputStream(tempArchive)) {
            IOUtils.copy(inputStream, outputStream);
        }

        archiveExtractor.extractArchive(tempArchive, context.getToolRoot());

        FileUtils.forceDelete(tempArchive);
    }

    private void executePostInstallationCommands(T toolConfiguration, ToolSpecificServiceContext context) throws IOException, InterruptedException {
        SimpleToolInfo toolInfo = ToolSpecificServices.collectToolInfo(toolConfiguration, context);

        Map<String, String> processEnvironment = ProcessEnvironmentHelper.createProcessEnvironmentFromToolInfo(toolInfo);

        for (PostExtractionCommand postInstallationCommand : toolConfiguration.getPostExtractionCommands()) {
            String executable = resolveExecutable(postInstallationCommand.getExecutableName(), toolInfo);

            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.environment().putAll(processEnvironment);
            processBuilder.inheritIO();
            processBuilder.command().add(executable);
            processBuilder.command().addAll(postInstallationCommand.getArguments());
            processBuilder.directory(context.getProjectRoot());
            Process process = processBuilder.start();
            process.waitFor();
        }
    }

    private String resolveExecutable(String executableName, SimpleToolInfo toolInfo) {
        File executable = ProcessEnvironmentHelper.resolveExecutableFromToolInfo(executableName, toolInfo);
        if (executable != null) {
            return executable.getAbsolutePath();
        } else {
            return executableName;
        }
    }

}
