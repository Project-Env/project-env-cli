package io.projectenv.core.commons.download;

import io.projectenv.core.commons.system.CPUArchitecture;
import io.projectenv.core.commons.system.OperatingSystem;
import org.immutables.value.Value;

import java.util.Map;

@Value.Immutable
public interface DownloadUrlDictionary {

    Map<String, String> getParameters();

    Map<String, Map<OperatingSystem, String>> getOperatingSystemSpecificParameters();

    Map<String, Map<CPUArchitecture, String>> getCPUArchitectureSpecificParameters();

}
