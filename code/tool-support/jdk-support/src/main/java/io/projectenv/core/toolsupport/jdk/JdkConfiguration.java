package io.projectenv.core.toolsupport.jdk;

import com.google.gson.annotations.SerializedName;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

@Gson.TypeAdapters(fieldNamingStrategy = true)
@Value.Immutable
public interface JdkConfiguration {

    String getDistribution();

    @SerializedName(value = "version", alternate = "distribution_version")
    String getVersion();

    List<String> getPostExtractionCommands();

}
