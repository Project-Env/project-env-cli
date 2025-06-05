package io.projectenv.core.toolsupport.spi.index;

import io.projectenv.core.commons.system.CpuArchitecture;
import io.projectenv.core.commons.system.OperatingSystem;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.Map;
import java.util.Set;

@Gson.TypeAdapters
@Value.Immutable
public interface ToolsIndexV2 {

    Map<String, Map<String, Map<OperatingSystem, Map<CpuArchitecture, String>>>> getJdkVersions();

    Map<String, Set<String>> getJdkDistributionSynonyms();

    Map<String, String> getGradleVersions();

    Map<String, String> getMavenVersions();

    Map<String, Map<OperatingSystem, Map<CpuArchitecture, String>>> getNodeVersions();

    Map<String, Map<OperatingSystem, String>> getClojureVersions();

}
