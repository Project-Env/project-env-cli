package io.projectenv.core.installer;

import io.projectenv.core.configuration.ToolConfiguration;
import io.projectenv.core.toolinfo.ToolInfo;

public interface ProjectToolInstaller<T extends ToolConfiguration, S extends ToolInfo> {

    S installTool(T configuration, ProjectToolInstallerContext context) throws ProjectToolInstallerException;

    boolean supportsTool(ToolConfiguration toolConfiguration);

}
