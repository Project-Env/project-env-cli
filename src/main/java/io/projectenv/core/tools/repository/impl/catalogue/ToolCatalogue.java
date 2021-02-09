package io.projectenv.core.tools.repository.impl.catalogue;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonSerialize(as = ImmutableToolCatalogue.class)
@JsonDeserialize(as = ImmutableToolCatalogue.class)
public interface ToolCatalogue {

    List<ToolEntry> getEntries();

}
