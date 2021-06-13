package io.projectenv.core.toolsupport.maven;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

@Gson.TypeAdapters(fieldNamingStrategy = true)
@Value.Immutable
public interface MavenConfiguration {

    String getVersion();

    String getGlobalSettingsFile();

    String getUserSettingsFile();

    List<String> getPostExtractionCommands();

}
