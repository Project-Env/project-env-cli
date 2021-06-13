package io.projectenv.core.toolsupport.git;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters(fieldNamingStrategy = true)
@Value.Immutable
public interface GitConfiguration {

    String getHooksDirectory();

}
