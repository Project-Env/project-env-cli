package io.projectenv.toolsupport.gradle;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

@Gson.TypeAdapters(fieldNamingStrategy = true)
@Value.Immutable
public interface GradleConfiguration {

    String getVersion();

    List<String> getPostExtractionCommands();

}
