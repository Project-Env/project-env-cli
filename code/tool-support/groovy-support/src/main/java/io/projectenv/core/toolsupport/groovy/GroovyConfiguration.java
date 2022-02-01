package io.projectenv.core.toolsupport.groovy;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

@Gson.TypeAdapters(fieldNamingStrategy = true)
@Value.Immutable
public interface GroovyConfiguration {

    String getVersion();

    List<String> getPostExtractionCommands();

}
