package io.projectenv.cli.configuration;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters(fieldNamingStrategy = true)
@Value.Immutable
public interface ProjectEnvBaseConfiguration {

    String getToolsDirectory();

}
