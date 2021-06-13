package io.projectenv.toolsupport.spi;

import io.projectenv.toolsupport.api.ToolInfo;

public interface ToolSupport<T> {

    String getToolIdentifier();

    ToolInfo prepareTool(T toolConfiguration, ToolSupportContext context) throws ToolSupportException;

}
