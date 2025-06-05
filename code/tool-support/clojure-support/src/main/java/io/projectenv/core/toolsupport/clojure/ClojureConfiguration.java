package io.projectenv.core.toolsupport.clojure;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

@Gson.TypeAdapters(fieldNamingStrategy = true)
@Value.Immutable
public interface ClojureConfiguration {

    String getVersion();

    List<String> getPostExtractionCommands();

}
