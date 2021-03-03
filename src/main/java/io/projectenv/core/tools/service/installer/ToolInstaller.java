package io.projectenv.core.tools.service.installer;

import io.projectenv.core.configuration.ToolConfiguration;
import io.projectenv.core.tools.service.ToolSpecificService;
import io.projectenv.core.tools.service.ToolSpecificServiceContext;

public interface ToolInstaller<T extends ToolConfiguration> extends ToolSpecificService<ToolConfiguration> {

    void installTool(T configuration, ToolSpecificServiceContext context) throws ToolInstallerException;

}
