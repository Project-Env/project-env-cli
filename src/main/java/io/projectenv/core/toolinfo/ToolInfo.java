package io.projectenv.core.toolinfo;

import org.immutables.value.Value;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Value.Immutable
public interface ToolInfo {

    String getToolName();

    File getLocation();

    Optional<File> getPrimaryExecutable();

    Map<String, File> getEnvironmentVariables();

    List<File> getPathElements();

}
