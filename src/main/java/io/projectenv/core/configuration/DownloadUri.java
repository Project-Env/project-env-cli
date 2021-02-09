package io.projectenv.core.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.projectenv.core.common.OperatingSystem;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableDownloadUri.class)
@JsonDeserialize(as = ImmutableDownloadUri.class)
public interface DownloadUri {

    @JsonProperty(required = true)
    String getDownloadUri();

    @Value.Default
    default OperatingSystem getTargetOs() {
        return OperatingSystem.ALL;
    }

}
