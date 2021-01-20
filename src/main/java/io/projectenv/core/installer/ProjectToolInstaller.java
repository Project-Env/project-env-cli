package io.projectenv.core.installer;

import io.projectenv.core.configuration.ToolConfiguration;
import io.projectenv.core.toolinfo.ToolInfo;

public interface ProjectToolInstaller<ToolConfigurationType extends ToolConfiguration, ToolInfoType extends ToolInfo> {

    ToolInfoType installTool(ToolConfigurationType configuration, ProjectToolInstallerContext context) throws Exception;

    boolean supportsTool(ToolConfiguration toolConfiguration);

}
