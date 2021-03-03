package io.projectenv.core.tools.info;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Value.Immutable
@JsonSerialize(as = ImmutableSimpleToolInfo.class)
@JsonDeserialize(as = ImmutableSimpleToolInfo.class)
public interface SimpleToolInfo extends ToolInfo {

    File getLocation();

    Optional<File> getPrimaryExecutable();

    Map<String, File> getEnvironmentVariables();

    List<File> getPathElements();

}
