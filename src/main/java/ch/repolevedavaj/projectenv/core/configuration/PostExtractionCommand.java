package ch.repolevedavaj.projectenv.core.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonSerialize(as = ImmutablePostExtractionCommand.class)
@JsonDeserialize(as = ImmutablePostExtractionCommand.class)
public interface PostExtractionCommand {

    @JsonProperty(required = true)
    String getExecutableName();

    List<String> getArguments();

}
