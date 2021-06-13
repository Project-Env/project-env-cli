package io.projectenv.toolsupport.nodejs;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

@Gson.TypeAdapters(fieldNamingStrategy = true)
@Value.Immutable
public interface NodeJsConfiguration {

    String getVersion();

    List<String> getPostExtractionCommands();

}
