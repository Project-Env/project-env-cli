package io.projectenv.core.toolsupport.nodejs;

import io.projectenv.core.commons.system.OperatingSystem;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Gson.TypeAdapters(fieldNamingStrategy = true)
@Value.Immutable
public interface GenericToolConfiguration {

    Optional<String> getPrimaryExecutable();

    Optional<String> getDownloadUrl();

    List<DownloadUrlConfiguration> getDownloadUrls();

    Map<String, String> getEnvironmentVariables();

    List<String> getPathElements();

    List<String> getPostExtractionCommands();

    @Gson.TypeAdapters(fieldNamingStrategy = true)
    @Value.Immutable
    interface DownloadUrlConfiguration {

        String getDownloadUrl();

        OperatingSystem getTargetOs();

    }

}
