package io.projectenv.toolsupport.spi;

import io.projectenv.toolsupport.spi.installation.LocalToolInstallationManager;
import org.immutables.value.Value;

import java.io.File;

@Value.Immutable
public interface ToolSupportContext {

    File getProjectRoot();

    LocalToolInstallationManager getLocalToolInstallationManager();

}
