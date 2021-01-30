package io.projectenv.core.toolinfo.collector;

import io.projectenv.core.configuration.ToolConfiguration;
import io.projectenv.core.toolinfo.ToolInfo;

public interface ToolInfoCollector<T extends ToolConfiguration, S extends ToolInfo> {

    S collectToolInfo(T toolConfiguration, ToolInfoCollectorContext context);

    boolean supportsTool(ToolConfiguration toolConfiguration);

}
