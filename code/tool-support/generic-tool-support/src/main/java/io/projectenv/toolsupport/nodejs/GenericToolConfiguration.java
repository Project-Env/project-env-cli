package io.projectenv.toolsupport.nodejs;

import io.projectenv.toolsupport.commons.system.OperatingSystem;
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

    interface DownloadUrlConfiguration {

        String getDownloadUrl();

        OperatingSystem getTargetOS();

    }

}
