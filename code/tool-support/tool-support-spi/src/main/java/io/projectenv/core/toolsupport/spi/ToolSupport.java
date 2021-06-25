package io.projectenv.core.toolsupport.spi;

import io.projectenv.core.cli.api.ToolInfo;

public interface ToolSupport<T> {

    String getToolIdentifier();

    ToolInfo prepareTool(T toolConfiguration, ToolSupportContext context) throws ToolSupportException;

}
