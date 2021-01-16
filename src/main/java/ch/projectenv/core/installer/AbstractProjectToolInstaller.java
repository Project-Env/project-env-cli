package ch.projectenv.core.installer;

import ch.projectenv.core.toolinfo.ToolInfo;
import ch.projectenv.core.archive.ArchiveExtractor;
import ch.projectenv.core.common.OperatingSystem;
import ch.projectenv.core.common.ProcessEnvironmentHelper;
import ch.projectenv.core.configuration.PostExtractionCommand;
import ch.projectenv.core.configuration.ToolConfiguration;
import ch.projectenv.core.toolinfo.collector.ToolInfoCollectors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

public abstract class AbstractProjectToolInstaller<ToolConfigurationType extends ToolConfiguration, ToolInfoType extends ToolInfo>
        implements ProjectToolInstaller<ToolConfigurationType, ToolInfoType> {

    private static final String TOOL_INSTALLATION_VERSION_MARKER = "project-env-tool-version.txt";
    public static final String TOOL_BINARIES_DIRECTORY_NAME = "binary";

    private final ArchiveExtractor archiveExtractor = new ArchiveExtractor();

    @Override
    public ToolInfoType installTool(ToolConfigurationType toolConfiguration, File toolDirectory) throws Exception {
        File toolBinariesDirectory = getToolBinariesDirectory(toolDirectory);

        if (isCurrentToolUpToDate(toolConfiguration, toolDirectory)) {
            return ToolInfoCollectors.collectToolInfo(toolConfiguration, toolBinariesDirectory);
        }

        cleanToolInstallationDirectory(toolBinariesDirectory);
        extractToolInstallation(toolConfiguration, toolBinariesDirectory);

        ToolInfoType toolInfo = ToolInfoCollectors.collectToolInfo(toolConfiguration, toolBinariesDirectory);
        executePostInstallationCommands(toolConfiguration, toolInfo);
        storeToolVersion(toolConfiguration, toolDirectory);

        return toolInfo;
    }

    @Override
    public boolean supportsTool(ToolConfiguration toolConfiguration) {
        return getToolConfigurationClass().isAssignableFrom(toolConfiguration.getClass());
    }

    protected abstract Class<ToolConfigurationType> getToolConfigurationClass();

    private File getToolBinariesDirectory(File toolInstallationDirectory) {
        return new File(toolInstallationDirectory, TOOL_BINARIES_DIRECTORY_NAME);
    }

    private boolean isCurrentToolUpToDate(ToolConfigurationType configuration, File toolInstallationDirectory) throws Exception {
        String currentToolVersion = getCurrentToolVersion(toolInstallationDirectory);
        String requestedToolVersion = getRequestedToolVersion(configuration);
        return StringUtils.equals(currentToolVersion, requestedToolVersion);
    }

    private String getCurrentToolVersion(File toolInstallationDirectory) throws Exception {
        File toolVersionFile = getToolVersionFile(toolInstallationDirectory);
        if (toolVersionFile.exists()) {
            return FileUtils.readFileToString(toolVersionFile, StandardCharsets.UTF_8);
        } else {
            return null;
        }
    }

    private File getToolVersionFile(File toolInstallationDirectory) {
        return new File(toolInstallationDirectory, TOOL_INSTALLATION_VERSION_MARKER);
    }

    private String getRequestedToolVersion(ToolConfigurationType configuration) {
        return getSystemSpecificDownloadUri(configuration).toString();
    }

    private void storeToolVersion(ToolConfigurationType configuration, File toolDirectory) throws Exception {
        FileUtils.write(getToolVersionFile(toolDirectory), getRequestedToolVersion(configuration), StandardCharsets.UTF_8);
    }

    private URI getSystemSpecificDownloadUri(ToolConfigurationType configuration) {
        OperatingSystem currentOS = OperatingSystem.getCurrentOS();

        return configuration
                .getDownloadUris()
                .stream()
                .filter(downloadUriConfiguration -> {
                    OperatingSystem targetOperatingSystem = downloadUriConfiguration.getTargetOs();

                    return targetOperatingSystem == OperatingSystem.ALL || targetOperatingSystem == currentOS;
                })
                .findFirst()
                .map((downloadUriConfiguration) -> URI.create(downloadUriConfiguration.getDownloadUri()))
                .orElseThrow(() -> new IllegalStateException("no download URI for OS " + currentOS + " available"));
    }

    private void cleanToolInstallationDirectory(File toolInstallationDirectory) throws Exception {
        if (toolInstallationDirectory.exists()) {
            FileUtils.forceDelete(toolInstallationDirectory);
        }

        FileUtils.forceMkdirParent(toolInstallationDirectory);
    }

    private void extractToolInstallation(ToolConfigurationType toolInstallationConfiguration, File toolInstallationDirectory) throws Exception {
        URI systemSpecificDownloadUri = getSystemSpecificDownloadUri(toolInstallationConfiguration);

        String archiveName = FilenameUtils.getName(systemSpecificDownloadUri.getPath());
        File tempArchive = Files.createTempFile(toolInstallationConfiguration.getToolName(), archiveName).toFile();

        try (InputStream inputStream = new BufferedInputStream(systemSpecificDownloadUri.toURL().openStream());
             OutputStream outputStream = new FileOutputStream(tempArchive)) {
            IOUtils.copy(inputStream, outputStream);
        }

        archiveExtractor.extractArchive(tempArchive, toolInstallationDirectory);

        FileUtils.forceDelete(tempArchive);
    }

    private void executePostInstallationCommands(ToolConfigurationType toolInstallationConfiguration, ToolInfoType toolInfo) throws Exception {
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
            Process process = processBuilder.start();
            process.waitFor();
        }
    }

}
