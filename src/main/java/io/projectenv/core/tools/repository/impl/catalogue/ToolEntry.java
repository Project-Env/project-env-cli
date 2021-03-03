package io.projectenv.core.tools.repository.impl.catalogue;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.projectenv.core.common.OperatingSystem;
import io.projectenv.core.configuration.ToolConfiguration;
import io.projectenv.core.tools.info.ToolInfo;
import org.immutables.value.Value;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = ToolEntry.JSON_TYPE_PROPERTY
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = JdkEntry.class, name = JdkEntry.JSON_TYPE),
        @JsonSubTypes.Type(value = MavenEntry.class, name = MavenEntry.JSON_TYPE),
        @JsonSubTypes.Type(value = GradleEntry.class, name = GradleEntry.JSON_TYPE),
        @JsonSubTypes.Type(value = NodeEntry.class, name = NodeEntry.JSON_TYPE),
        @JsonSubTypes.Type(value = GenericToolEntry.class, name = GenericToolEntry.JSON_TYPE),
        @JsonSubTypes.Type(value = GitHooksEntry.class, name = GitHooksEntry.JSON_TYPE)
})
@Value.Immutable
public interface ToolEntry {

    String JSON_TYPE_PROPERTY = "type";

    String getId();

    OperatingSystem getTargetOS();

    ToolConfiguration getToolConfiguration();

    ToolInfo getToolInstallationInfo();

}
