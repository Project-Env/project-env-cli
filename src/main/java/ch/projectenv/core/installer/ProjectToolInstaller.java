package ch.projectenv.core.installer;

import ch.projectenv.core.toolinfo.ToolInfo;
import ch.projectenv.core.configuration.ToolConfiguration;

import java.io.File;

public interface ProjectToolInstaller<ToolConfigurationType extends ToolConfiguration, ToolInfoType extends ToolInfo> {

    ToolInfoType installTool(ToolConfigurationType configuration, File toolsDirectory) throws Exception;

    boolean supportsTool(ToolConfiguration toolConfiguration);

}
