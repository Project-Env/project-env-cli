package io.projectenv.core.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public interface SimpleToolConfiguration extends ToolConfiguration {

    @JsonProperty(required = true)
    List<DownloadUri> getDownloadUris();

    Map<String, String> getEnvironmentVariables();

    List<String> getPathElements();

    List<PostExtractionCommand> getPostExtractionCommands();

}
