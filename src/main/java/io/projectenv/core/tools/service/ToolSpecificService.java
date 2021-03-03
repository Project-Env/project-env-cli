package io.projectenv.core.tools.service;

public interface ToolSpecificService<T> {

    boolean supportsTool(T tool);

}
