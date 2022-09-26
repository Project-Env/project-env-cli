package io.projectenv.core.toolsupport.spi;

import java.util.Optional;

public interface ToolSupport<T> {

    String getToolIdentifier();

    Class<T> getToolConfigurationClass();

    ToolInfo prepareTool(T toolConfiguration, ToolSupportContext context) throws ToolSupportException;

    default Optional<ToolUpgradeInfo> upgradeToolVersion(T toolConfiguration, ToolSupportContext context) {
        return Optional.empty();
    }

}
