package io.projectenv.core.tools.repository.impl.catalogue;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.projectenv.core.configuration.JdkConfiguration;
import io.projectenv.core.tools.info.JdkInfo;
import org.immutables.value.Value;

@Value.Immutable
@JsonTypeName(JdkEntry.JSON_TYPE)
@JsonSerialize(as = ImmutableJdkEntry.class)
@JsonDeserialize(as = ImmutableJdkEntry.class)
public interface JdkEntry extends ToolEntry {

    String JSON_TYPE = "jdk";

    @Override
    JdkConfiguration getToolConfiguration();

    @Override
    JdkInfo getToolInstallationInfo();

}