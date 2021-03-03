package io.projectenv.core.tools.service.collector.impl;

import io.projectenv.core.common.ProcessEnvironmentHelper;
import io.projectenv.core.configuration.SimpleToolConfiguration;
import io.projectenv.core.configuration.ToolConfiguration;
import io.projectenv.core.tools.info.ImmutableSimpleToolInfo;
import io.projectenv.core.tools.info.SimpleToolInfo;
import io.projectenv.core.tools.service.ToolSpecificServiceContext;
import io.projectenv.core.tools.service.collector.ToolInfoCollector;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractSimpleToolInfoCollector<T extends SimpleToolConfiguration, S extends SimpleToolInfo>
        implements ToolInfoCollector<T, S> {

    @Override
    public S collectToolInfo(T toolConfiguration, ToolSpecificServiceContext context) {
        File relevantToolBinariesDirectory = getRelevantToolBinariesDirectory(context);

        SimpleToolInfo baseToolInfo = collectBaseToolInfo(toolConfiguration, relevantToolBinariesDirectory);

        return collectToolSpecificInfo(baseToolInfo, toolConfiguration, context);
    }

    @Override
    public boolean supportsTool(ToolConfiguration toolConfiguration) {
        return getToolConfigurationClass().isAssignableFrom(toolConfiguration.getClass());
    }

    protected abstract Class<T> getToolConfigurationClass();

    protected File getRelevantToolBinariesDirectory(ToolSpecificServiceContext context) {
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

    private SimpleToolInfo collectBaseToolInfo(T toolConfiguration, File relevantToolBinariesDirectory) {
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

        return ImmutableSimpleToolInfo
                .builder()
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

    protected abstract S collectToolSpecificInfo(SimpleToolInfo baseToolInfo, T toolConfiguration, ToolSpecificServiceContext context);

}
