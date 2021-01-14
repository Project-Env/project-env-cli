package ch.repolevedavaj.projectenv.core.toolinfo.collector;

import ch.repolevedavaj.projectenv.core.common.ProcessEnvironmentHelper;
import ch.repolevedavaj.projectenv.core.configuration.ToolConfiguration;
import ch.repolevedavaj.projectenv.core.toolinfo.ImmutableToolInfo;
import ch.repolevedavaj.projectenv.core.toolinfo.ToolInfo;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractToolInfoCollector<ToolConfigurationType extends ToolConfiguration, ToolInfoType extends ToolInfo>
        implements ToolInfoCollector<ToolConfigurationType, ToolInfoType> {

    @Override
    public ToolInfoType collectToolInfo(ToolConfigurationType toolConfiguration, File toolBinariesDirectory) {
        File relevantToolBinariesDirectory = getRelevantToolBinariesDirectory(toolBinariesDirectory);

        ToolInfo baseToolInfo = collectBaseToolInfo(toolConfiguration, relevantToolBinariesDirectory);

        return collectToolSpecificInfo(baseToolInfo, toolConfiguration);
    }

    @Override
    public boolean supportsTool(ToolConfiguration toolConfiguration) {
        return getToolConfigurationClass().isAssignableFrom(toolConfiguration.getClass());
    }

    protected abstract Class<ToolConfigurationType> getToolConfigurationClass();

    protected File getRelevantToolBinariesDirectory(File toolBinariesDirectory) {
        List<File> files = Optional.ofNullable(toolBinariesDirectory.listFiles())
                .map(Arrays::asList)
                .orElse(List.of());

        if (files.size() == 1) {
            return files.get(0);
        } else {
            return toolBinariesDirectory;
        }
    }

    private ToolInfo collectBaseToolInfo(ToolConfigurationType toolConfiguration, File relevantToolBinariesDirectory) {
        Map<String, File> environmentVariables = new HashMap<>();
        environmentVariables.putAll(toolConfiguration.getEnvironmentVariables()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> new File(relevantToolBinariesDirectory, entry.getValue()))));
        environmentVariables.putAll(getAdditionalExports()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, export -> new File(relevantToolBinariesDirectory, export.getValue()))));

        List<File> pathElements = new ArrayList<>();
        pathElements.addAll(toolConfiguration.getPathElements()
                .stream()
                .map(pathElement -> new File(relevantToolBinariesDirectory, pathElement))
                .collect(Collectors.toList()));
        pathElements.addAll(getAdditionalPathElements()
                .stream()
                .map(pathElement -> new File(relevantToolBinariesDirectory, pathElement))
                .collect(Collectors.toList()));

        Optional<File> primaryExecutable = Optional.ofNullable(getPrimaryExecutableName())
                .map(primaryExecutableName -> {
                    File executable = ProcessEnvironmentHelper.resolveExecutableFromPathElements(primaryExecutableName, pathElements);
                    if (executable == null) {
                        throw new IllegalStateException("failed to resolve primary executable " + primaryExecutableName);
                    }

                    return executable;
                });

        return ImmutableToolInfo
                .builder()
                .toolName(toolConfiguration.getToolName())
                .location(relevantToolBinariesDirectory)
                .putAllEnvironmentVariables(environmentVariables)
                .addAllPathElements(pathElements)
                .primaryExecutable(primaryExecutable)
                .build();
    }

    protected Map<String, String> getAdditionalExports() {
        return Map.of();
    }

    protected List<String> getAdditionalPathElements() {
        return List.of();
    }

    protected String getPrimaryExecutableName() {
        return null;
    }

    protected abstract ToolInfoType collectToolSpecificInfo(ToolInfo baseToolInfo, ToolConfigurationType toolConfiguration);

}
