package io.projectenv.core.tools.info;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableJdkInfo.class)
@JsonDeserialize(as = ImmutableJdkInfo.class)
public interface JdkInfo extends SimpleToolInfo {
}
