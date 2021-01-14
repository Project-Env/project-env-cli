package ch.repolevedavaj.projectenv.core.configuration;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
@JsonSerialize(as = ImmutableMavenConfiguration.class)
@JsonDeserialize(as = ImmutableMavenConfiguration.class)
public interface MavenConfiguration extends ToolConfiguration {

    Optional<String> getGlobalSettingsFile();

    Optional<String> getUserSettingsFile();

}
