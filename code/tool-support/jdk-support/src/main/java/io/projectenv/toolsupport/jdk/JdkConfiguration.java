package io.projectenv.toolsupport.jdk;

import com.google.gson.annotations.SerializedName;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

@Gson.TypeAdapters(fieldNamingStrategy = true)
@Value.Immutable
public interface JdkConfiguration {

    String getJavaVersion();

    JdkDistribution getDistribution();

    String getDistributionVersion();

    List<String> getPostExtractionCommands();

    enum JdkDistribution {
        @SerializedName("AdoptOpenJDK")
        ADOPTOPENJDK,
        @SerializedName("GraalVM")
        GRAALVM
    }

}
