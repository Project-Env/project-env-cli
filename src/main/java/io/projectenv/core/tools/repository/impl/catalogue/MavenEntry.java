package io.projectenv.core.tools.repository.impl.catalogue;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.projectenv.core.configuration.MavenConfiguration;
import io.projectenv.core.tools.info.MavenInfo;
import org.immutables.value.Value;

@Value.Immutable
@JsonTypeName(MavenEntry.JSON_TYPE)
@JsonSerialize(as = ImmutableMavenEntry.class)
@JsonDeserialize(as = ImmutableMavenEntry.class)
public interface MavenEntry extends ToolEntry {

    String JSON_TYPE = "maven";

    @Override
    MavenConfiguration getToolConfiguration();

    @Override
    MavenInfo getToolInstallationInfo();

}
