package io.projectenv.core.tools.info;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.io.File;
import java.util.Optional;

@Value.Immutable
@JsonSerialize(as = ImmutableMavenInfo.class)
@JsonDeserialize(as = ImmutableMavenInfo.class)
public interface MavenInfo extends ToolInfo {

    Optional<File> getGlobalSettingsFile();

    Optional<File> getUserSettingsFile();

}
