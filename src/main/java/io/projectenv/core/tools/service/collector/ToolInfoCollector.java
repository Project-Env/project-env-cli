package io.projectenv.core.tools.service.collector;

import io.projectenv.core.configuration.ToolConfiguration;
import io.projectenv.core.tools.info.ToolInfo;
import io.projectenv.core.tools.service.ToolSpecificService;
import io.projectenv.core.tools.service.ToolSpecificServiceContext;

public interface ToolInfoCollector<T extends ToolConfiguration, S extends ToolInfo> extends ToolSpecificService<ToolConfiguration> {

    S collectToolInfo(T toolConfiguration, ToolSpecificServiceContext context);

}
