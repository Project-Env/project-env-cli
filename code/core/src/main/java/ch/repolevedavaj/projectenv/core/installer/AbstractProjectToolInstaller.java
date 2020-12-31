package ch.repolevedavaj.projectenv.core.installer;

import ch.repolevedavaj.projectenv.core.ImmutableProjectToolDetails;
import ch.repolevedavaj.projectenv.core.ProjectToolDetails;
import ch.repolevedavaj.projectenv.core.ProjectToolType;
import ch.repolevedavaj.projectenv.core.archive.ArchiveExtractorFactory;
import ch.repolevedavaj.projectenv.core.configuration.BaseInstallationConfiguration;
import ch.repolevedavaj.projectenv.core.configuration.BaseInstallationConfiguration.DownloadUris;
import ch.repolevedavaj.projectenv.core.configuration.BaseInstallationConfiguration.Exports;
import ch.repolevedavaj.projectenv.core.configuration.BaseInstallationConfiguration.Exports.Export;
import ch.repolevedavaj.projectenv.core.configuration.BaseInstallationConfiguration.PostInstallationCommands;
import ch.repolevedavaj.projectenv.core.configuration.BaseInstallationConfiguration.PostInstallationCommands.PostInstallationCommand;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractProjectToolInstaller<ToolInstallationConfiguration extends BaseInstallationConfiguration> implements ProjectToolInstaller<ToolInstallationConfiguration> {

    private static final String TOOL_INSTALLATION_VERSION_MARKER = "project-env-tool-version.txt";

    @Override
    public ProjectToolDetails installTool(ToolInstallationConfiguration toolInstallationConfiguration, File toolsDirectory) throws Exception {
        File toolInstallationDirectory = getToolInstallationDirectory(toolInstallationConfiguration, toolsDirectory);
        if (isCurrentToolUpToDate(toolInstallationConfiguration, toolInstallationDirectory)) {
            return createProjectToolInstallationDetails(toolInstallationConfiguration, toolInstallationDirectory);
        }

        cleanToolInstallationDirectory(toolInstallationDirectory);
        extractToolInstallation(toolInstallationConfiguration, toolInstallationDirectory);

        ProjectToolDetails installationDetails = createProjectToolInstallationDetails(toolInstallationConfiguration, toolInstallationDirectory);
        executePostInstallationCommands(toolInstallationConfiguration, installationDetails);
        storeToolVersion(toolInstallationConfiguration, toolInstallationDirectory);

        return installationDetails;
    }

    private File getToolInstallationDirectory(ToolInstallationConfiguration toolInstallationConfiguration, File toolInstallationDirectory) {
        return new File(toolInstallationDirectory, toolInstallationConfiguration.getName());
    }

    private boolean isCurrentToolUpToDate(ToolInstallationConfiguration configuration, File toolInstallationDirectory) throws Exception {
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

    private String getRequestedToolVersion(ToolInstallationConfiguration configuration) {
        return getSystemSpecificDownloadUri(configuration).toString();
    }

    private void storeToolVersion(ToolInstallationConfiguration configuration, File toolDirectory) throws Exception {
        FileUtils.write(getToolVersionFile(toolDirectory), getRequestedToolVersion(configuration), StandardCharsets.UTF_8);
    }

    private URI getSystemSpecificDownloadUri(ToolInstallationConfiguration configuration) {
        DownloadUris downloadUris = configuration.getDownloadUris();

        if (StringUtils.isNotEmpty(downloadUris.getSystemIndependent())) {
            return URI.create(downloadUris.getSystemIndependent());
        }

        if (SystemUtils.IS_OS_MAC) {
            return URI.create(downloadUris.getMacos());
        }

        if (SystemUtils.IS_OS_LINUX) {
            return URI.create(downloadUris.getLinux());
        }

        if (SystemUtils.IS_OS_WINDOWS) {
            return URI.create(downloadUris.getWindows());
        }

        throw new IllegalStateException("unsupported OS " + SystemUtils.OS_NAME);
    }

    protected ProjectToolDetails createProjectToolInstallationDetails(ToolInstallationConfiguration toolInstallationConfiguration, File toolInstallationDirectory) {
        File relevantProjectToolRoot = getRelevantProjectToolRoot(toolInstallationDirectory);

        return ImmutableProjectToolDetails
                .builder()
                .type(getProjectToolType())
                .location(relevantProjectToolRoot)
                .putAllExports(Optional.ofNullable(toolInstallationConfiguration.getExports())
                        .map(Exports::getExport)
                        .orElse(List.of())
                        .stream()
                        .collect(Collectors.toMap(Export::getName, export -> new File(relevantProjectToolRoot, export.getValue()))))
                .putAllExports(getAdditionalExports()
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, export -> new File(relevantProjectToolRoot, export.getValue()))))
                .addAllPathElements(Optional.ofNullable(toolInstallationConfiguration.getPathElements())
                        .map(BaseInstallationConfiguration.PathElements::getPathElement)
                        .orElse(List.of())
                        .stream()
                        .map(pathElement -> new File(relevantProjectToolRoot, pathElement))
                        .collect(Collectors.toList()))
                .addAllPathElements(getAdditionalPathElements()
                        .stream()
                        .map(pathElement -> new File(relevantProjectToolRoot, pathElement))
                        .collect(Collectors.toList()))
                .build();
    }

    private void cleanToolInstallationDirectory(File toolInstallationDirectory) throws Exception {
        if (toolInstallationDirectory.exists()) {
            FileUtils.forceDelete(toolInstallationDirectory);
        }

        FileUtils.forceMkdirParent(toolInstallationDirectory);
    }

    private void extractToolInstallation(ToolInstallationConfiguration toolInstallationConfiguration, File toolInstallationDirectory) throws Exception {
        URI systemSpecificDownloadUri = getSystemSpecificDownloadUri(toolInstallationConfiguration);

        ArchiveExtractorFactory
                .getArchiveExtractor(systemSpecificDownloadUri)
                .extractArchive(systemSpecificDownloadUri, toolInstallationDirectory);
    }

    private void executePostInstallationCommands(ToolInstallationConfiguration toolInstallationConfiguration, ProjectToolDetails projectToolDetails) throws Exception {
        List<PostInstallationCommand> postInstallationCommands = Optional
                .ofNullable(toolInstallationConfiguration.getPostInstallationCommands())
                .map(PostInstallationCommands::getPostInstallationCommand)
                .orElse(List.of());

        Map<String, String> processEnvironment = projectToolDetails.getExports()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getAbsolutePath()));

        String pathExtension = projectToolDetails.getPathElements()
                .stream()
                .map(this::toCanonicalPath)
                .collect(Collectors.joining(":"));
        processEnvironment.put("PATH", pathExtension + ":" + System.getenv("PATH"));

        for (PostInstallationCommand postInstallationCommand : postInstallationCommands) {
            File executable = resolveExecutable(postInstallationCommand.getExecutable(), projectToolDetails);

            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.environment().putAll(processEnvironment);
            processBuilder.inheritIO();
            processBuilder.command().add(executable.getAbsolutePath());
            processBuilder.command().addAll(postInstallationCommand.getArguments().getArgument());
            Process process = processBuilder.start();
            process.waitFor();
        }
    }

    private String toCanonicalPath(File file) {
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File resolveExecutable(String executable, ProjectToolDetails projectToolDetails) {
        for (File pathElement : projectToolDetails.getPathElements()) {
            File executableCandidate = new File(pathElement, executable);
            if (executableCandidate.exists()) {
                return executableCandidate;
            }
        }

        throw new IllegalArgumentException("failed to resolve executable " + executable);
    }

    protected File getRelevantProjectToolRoot(File toolInstallationDirectory) {
        List<File> files = Optional.ofNullable(toolInstallationDirectory.listFiles((file) -> !StringUtils.equals(file.getName(), TOOL_INSTALLATION_VERSION_MARKER)))
                .map(Arrays::asList)
                .orElse(List.of());

        if (files.size() == 1) {
            return files.get(0);
        } else {
            return toolInstallationDirectory;
        }
    }

    protected Map<String, String> getAdditionalExports() {
        return Map.of();
    }

    protected List<String> getAdditionalPathElements() {
        return List.of();
    }

    protected abstract ProjectToolType getProjectToolType();

}
