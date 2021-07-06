package io.projectenv.core.toolsupport.maven;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

@Gson.TypeAdapters(fieldNamingStrategy = true)
@Value.Immutable
public interface MavenConfiguration {

    String getVersion();

    Optional<String> getGlobalSettingsFile();

    Optional<String> getUserSettingsFile();

    List<String> getPostExtractionCommands();

}
