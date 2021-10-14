package io.projectenv.core.toolsupport.jdk.download.impl.discoapi;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters(fieldNamingStrategy = true)
@Value.Immutable
public interface DiscoApiJdkPackage {

    String getEphemeralId();

}
