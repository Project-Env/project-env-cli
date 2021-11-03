package io.projectenv.core.toolsupport.spi;

import org.immutables.value.Value;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Value.Immutable
public interface ToolInfo {

    Optional<File> getToolBinariesRoot();

    Optional<File> getPrimaryExecutable();

    Map<String, File> getEnvironmentVariables();

    List<File> getPathElements();

    List<File> getHandledProjectResources();

    Map<String, File> getUnhandledProjectResources();

}
