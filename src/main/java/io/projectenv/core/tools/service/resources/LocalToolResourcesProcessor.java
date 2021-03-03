package io.projectenv.core.tools.service.resources;

import io.projectenv.core.tools.info.ToolInfo;
import io.projectenv.core.tools.service.ToolSpecificService;
import io.projectenv.core.tools.service.ToolSpecificServiceContext;

public interface LocalToolResourcesProcessor<T extends ToolInfo> extends ToolSpecificService<ToolInfo> {

    void processLocalToolResources(T toolInfo, ToolSpecificServiceContext context) throws LocalToolResourcesProcessorException;

}
