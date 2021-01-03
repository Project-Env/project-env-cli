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
import ch.repolevedavaj.projectenv.core.os.OS;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractProjectToolInstaller<ToolInstallationConfiguration extends BaseInstallationConfiguration> implements ProjectToolInstaller<ToolInstallationConfiguration> {

    private static final Map<OS, List<String>> OS_EXECUTABLE_EXTENSIONS = Map.of(
            OS.WINDOWS, List.of(".exe", ".cmd")
    );

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

        switch (OS.getCurrentOS()) {
            case MACOS:
                return URI.create(downloadUris.getMacos());
            case WINDOWS:
                return URI.create(downloadUris.getWindows());
            case LINUX:
                return URI.create(downloadUris.getLinux());
            default:
                throw new IllegalStateException("unsupported OS " + SystemUtils.OS_NAME);
        }
    }

    protected ProjectToolDetails createProjectToolInstallationDetails(ToolInstallationConfiguration toolInstallationConfiguration, File toolInstallationDirectory) {
        File relevantProjectToolRoot = getRelevantProjectToolRoot(toolInstallationDirectory);

        Map<String, File> exports = new HashMap<>();
        exports.putAll(Optional.ofNullable(toolInstallationConfiguration.getExports())
                .map(Exports::getExport)
                .orElse(List.of())
                .stream()
                .collect(Collectors.toMap(Export::getName, export -> new File(relevantProjectToolRoot, export.getValue()))));
        exports.putAll(getAdditionalExports()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, export -> new File(relevantProjectToolRoot, export.getValue()))));

        List<File> pathElements = new ArrayList<>();
        pathElements.addAll(Optional.ofNullable(toolInstallationConfiguration.getPathElements())
                .map(BaseInstallationConfiguration.PathElements::getPathElement)
                .orElse(List.of())
                .stream()
                .map(pathElement -> new File(relevantProjectToolRoot, pathElement))
                .collect(Collectors.toList()));
        pathElements.addAll(getAdditionalPathElements()
                .stream()
                .map(pathElement -> new File(relevantProjectToolRoot, pathElement))
                .collect(Collectors.toList()));

        Optional<File> primaryExecutable = Optional.ofNullable(getPrimaryExecutableName())
                .map(primaryExecutableName -> resolveExecutable(primaryExecutableName, pathElements));

        return ImmutableProjectToolDetails
                .builder()
                .type(getProjectToolType())
                .location(relevantProjectToolRoot)
                .putAllExports(exports)
                .addAllPathElements(pathElements)
                .primaryExecutable(primaryExecutable)
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
        processEnvironment.put("PATH", pathExtension + File.pathSeparator + System.getenv("PATH"));

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
        return resolveExecutable(executable, projectToolDetails.getPathElements());
    }

    protected File resolveExecutable(String executable, List<File> locations) {
        List<String> possibleExtensions = new ArrayList<>();
        if (OS_EXECUTABLE_EXTENSIONS.containsKey(OS.getCurrentOS())) {
            possibleExtensions.addAll(OS_EXECUTABLE_EXTENSIONS.get(OS.getCurrentOS()));
        }
        possibleExtensions.add(StringUtils.EMPTY);

        for (String possibleExtension : possibleExtensions) {
            String executableWithExtension = executable + possibleExtension;

            for (File pathElement : locations) {
                File executableCandidate = new File(pathElement, executableWithExtension);
                if (executableCandidate.exists()) {
                    return executableCandidate;
                }
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

    protected String getPrimaryExecutableName() {
        return null;
    }

}
