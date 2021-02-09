package io.projectenv.core.tools.info;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableGradleInfo.class)
@JsonDeserialize(as = ImmutableGradleInfo.class)
public interface GradleInfo extends ToolInfo {
}
