package io.projectenv.core.toolsupport.jdk.download.impl.discoapi;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

@Gson.TypeAdapters(fieldNamingStrategy = true)
@Value.Immutable
public interface DiscoApiDistribution {

    String getName();

    String getApiParameter();

    List<String> getSynonyms();

}
