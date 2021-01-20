package io.projectenv.core.toolinfo;

import org.immutables.value.Value;

import java.io.File;
import java.util.Optional;

@Value.Immutable
public interface MavenInfo extends ToolInfo {

    Optional<File> getGlobalSettingsFile();

    Optional<File> getUserSettingsFile();

}
