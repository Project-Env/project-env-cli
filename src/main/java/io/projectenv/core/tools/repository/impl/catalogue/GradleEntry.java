package io.projectenv.core.tools.repository.impl.catalogue;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.projectenv.core.configuration.GradleConfiguration;
import io.projectenv.core.tools.info.GradleInfo;
import org.immutables.value.Value;

@Value.Immutable
@JsonTypeName(GradleEntry.JSON_TYPE)
@JsonSerialize(as = ImmutableGradleEntry.class)
@JsonDeserialize(as = ImmutableGradleEntry.class)
public interface GradleEntry extends ToolEntry {

    String JSON_TYPE = "gradle";

    @Override
    GradleConfiguration getToolConfiguration();

    @Override
    GradleInfo getToolInstallationInfo();

}
