package io.projectenv.core.tools.collector;

import io.projectenv.core.configuration.ToolConfiguration;
import io.projectenv.core.tools.info.ToolInfo;

public interface ToolInfoCollector<T extends ToolConfiguration, S extends ToolInfo> {

    S collectToolInfo(T toolConfiguration, ToolInfoCollectorContext context);

    boolean supportsTool(ToolConfiguration toolConfiguration);

}
