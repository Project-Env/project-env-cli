package io.projectenv.core.toolsupport.jdk;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

@Gson.TypeAdapters(fieldNamingStrategy = true)
@Value.Immutable
public interface JdkConfiguration {

    String getDistribution();

    String getDistributionVersion();

    List<String> getPostExtractionCommands();

}
