package io.projectenv.core.tools.collector.impl;

import io.projectenv.core.common.ProcessEnvironmentHelper;
import io.projectenv.core.configuration.ToolConfiguration;
import io.projectenv.core.tools.collector.ToolInfoCollector;
import io.projectenv.core.tools.collector.ToolInfoCollectorContext;
import io.projectenv.core.tools.info.ImmutableToolInfo;
import io.projectenv.core.tools.info.ToolInfo;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractToolInfoCollector<T extends ToolConfiguration, S extends ToolInfo>
        implements ToolInfoCollector<T, S> {

    @Override
    public S collectToolInfo(T toolConfiguration, ToolInfoCollectorContext context) {
        File relevantToolBinariesDirectory = getRelevantToolBinariesDirectory(context);

        ToolInfo baseToolInfo = collectBaseToolInfo(toolConfiguration, relevantToolBinariesDirectory);

        return collectToolSpecificInfo(baseToolInfo, toolConfiguration, context);
    }

    @Override
    public boolean supportsTool(ToolConfiguration toolConfiguration) {
        return getToolConfigurationClass().isAssignableFrom(toolConfiguration.getClass());
    }

    protected abstract Class<T> getToolConfigurationClass();

    protected File getRelevantToolBinariesDirectory(ToolInfoCollectorContext context) {
        File toolBinariesRoot = context.getToolRoot();

        List<File> files = Optional.ofNullable(toolBinariesRoot.listFiles())
                .map(Arrays::asList)
                .orElse(List.of());

        if (files.size() == 1) {
            return files.get(0);
        } else {
            return toolBinariesRoot;
        }
    }

    private ToolInfo collectBaseToolInfo(T toolConfiguration, File relevantToolBinariesDirectory) {
        Map<String, File> environmentVariables = new HashMap<>();
        environmentVariables.putAll(createFileMap(toolConfiguration.getEnvironmentVariables(), relevantToolBinariesDirectory));
        environmentVariables.putAll(createFileMap(getAdditionalExports(), relevantToolBinariesDirectory));

        List<File> pathElements = new ArrayList<>();
        pathElements.addAll(createFileList(toolConfiguration.getPathElements(), relevantToolBinariesDirectory));
        pathElements.addAll(createFileList(getAdditionalPathElements(), relevantToolBinariesDirectory));

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


    private Map<String, File> createFileMap(Map<String, String> rawMap, File parent) {
        return rawMap
                .entrySet()
                .stream()
                .map(entry -> Pair.of(entry.getKey(), entry.getValue()))
                .map(pair -> Pair.of(pair.getLeft(), new File(parent, pair.getRight())))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    private List<File> createFileList(List<String> rawList, File parent) {
        return rawList
                .stream()
                .map(value -> new File(parent, value))
                .collect(Collectors.toList());
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

    protected abstract S collectToolSpecificInfo(ToolInfo baseToolInfo, T toolConfiguration, ToolInfoCollectorContext context);

}
