package io.projectenv.toolsupport.api;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Gson.TypeAdapters(fieldNamingStrategy = true)
@Value.Immutable
public interface ToolInfo {

    Optional<File> getToolBinariesRoot();

    Optional<File> getPrimaryExecutable();

    Map<String, File> getEnvironmentVariables();

    List<File> getPathElements();

    List<File> getHandledProjectResources();

    Map<String, File> getUnhandledProjectResources();

}
