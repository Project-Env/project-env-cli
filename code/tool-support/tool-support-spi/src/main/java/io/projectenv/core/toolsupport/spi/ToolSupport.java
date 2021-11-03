package io.projectenv.core.toolsupport.spi;

public interface ToolSupport<T> {

    String getToolIdentifier();

    ToolInfo prepareTool(T toolConfiguration, ToolSupportContext context) throws ToolSupportException;

}
