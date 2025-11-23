package io.projectenv.core.toolsupport.spi;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Gson.TypeAdapters
@Value.Immutable
public interface ToolInfo {

    Optional<File> getToolBinariesRoot();

    Optional<File> getPrimaryExecutable();

    Map<String, File> getEnvironmentVariables();

    List<File> getPathElements();

    List<File> getHandledProjectResources();

    Map<String, File> getUnhandledProjectResources();

    /**
     * Tool-specific structured metadata.
     * <p>
     * Examples:
     * <ul>
     *   <li>Maven: "version", "globalSettingsFile", "userSettingsFile"</li>
     *   <li>JDK: "version", "distribution"</li>
     * </ul>
     *
     * @return map of tool-specific metadata (empty by default)
     */
    Map<String, Object> getToolSpecificMetadata();

}
