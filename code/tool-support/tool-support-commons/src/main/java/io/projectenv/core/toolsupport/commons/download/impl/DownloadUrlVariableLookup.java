package io.projectenv.core.toolsupport.commons.download.impl;

import io.projectenv.core.toolsupport.commons.system.CPUArchitecture;
import io.projectenv.core.toolsupport.commons.system.OperatingSystem;
import io.projectenv.core.toolsupport.commons.download.DownloadUrlDictionary;
import org.apache.commons.text.lookup.StringLookup;

import java.util.Optional;

public class DownloadUrlVariableLookup implements StringLookup {

    private final DownloadUrlDictionary dictionary;

    public DownloadUrlVariableLookup(DownloadUrlDictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public String lookup(String key) {
        return Optional.<String>empty()
                .or(() -> getFromParameters(key))
                .or(() -> getFromOperatingSystemSpecificParameters(key))
                .or(() -> getFromCPUArchitectureSpecificParameters(key))
                .orElse(null);
    }

    private Optional<String> getFromParameters(String key) {
        return Optional.ofNullable(dictionary.getParameters().get(key));
    }

    private Optional<String> getFromOperatingSystemSpecificParameters(String key) {
        return Optional.ofNullable(dictionary.getOperatingSystemSpecificParameters().get(key))
                .map(value -> value.get(OperatingSystem.getCurrentOperatingSystem()));
    }

    private Optional<String> getFromCPUArchitectureSpecificParameters(String key) {
        return Optional.ofNullable(dictionary.getCPUArchitectureSpecificParameters().get(key))
                .map(value -> value.get(CPUArchitecture.getCurrentCPUArchitecture()));
    }

}
