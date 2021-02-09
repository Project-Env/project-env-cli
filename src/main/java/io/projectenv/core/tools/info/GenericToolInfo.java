package io.projectenv.core.tools.info;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableGenericToolInfo.class)
@JsonDeserialize(as = ImmutableGenericToolInfo.class)
public interface GenericToolInfo extends ToolInfo {
}
