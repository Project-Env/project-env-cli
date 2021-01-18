package ch.projectenv.core.installer;

import ch.projectenv.core.configuration.ToolConfiguration;
import ch.projectenv.core.toolinfo.ToolInfo;

public interface ProjectToolInstaller<ToolConfigurationType extends ToolConfiguration, ToolInfoType extends ToolInfo> {

    ToolInfoType installTool(ToolConfigurationType configuration, ProjectToolInstallerContext context) throws Exception;

    boolean supportsTool(ToolConfiguration toolConfiguration);

}
