package io.projectenv.core.toolsupport.spi;

import io.projectenv.core.toolsupport.api.ToolInfo;

public interface ToolSupport<T> {

    String getToolIdentifier();

    ToolInfo prepareTool(T toolConfiguration, ToolSupportContext context) throws ToolSupportException;

}
