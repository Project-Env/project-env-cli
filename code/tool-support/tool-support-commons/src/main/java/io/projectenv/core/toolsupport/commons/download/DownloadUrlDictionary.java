package io.projectenv.core.toolsupport.commons.download;

import io.projectenv.core.toolsupport.commons.system.CPUArchitecture;
import io.projectenv.core.toolsupport.commons.system.OperatingSystem;
import org.immutables.value.Value;

import java.util.Map;

@Value.Immutable
public interface DownloadUrlDictionary {

    Map<String, String> getParameters();

    Map<String, Map<OperatingSystem, String>> getOperatingSystemSpecificParameters();

    Map<String, Map<CPUArchitecture, String>> getCPUArchitectureSpecificParameters();

}
