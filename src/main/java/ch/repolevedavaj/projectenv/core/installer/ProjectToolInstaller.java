package ch.repolevedavaj.projectenv.core.installer;

import ch.repolevedavaj.projectenv.core.configuration.ToolConfiguration;
import ch.repolevedavaj.projectenv.core.toolinfo.ToolInfo;

import java.io.File;

public interface ProjectToolInstaller<ToolConfigurationType extends ToolConfiguration, ToolInfoType extends ToolInfo> {

    ToolInfoType installTool(ToolConfigurationType configuration, File toolsDirectory) throws Exception;

    boolean supportsTool(ToolConfiguration toolConfiguration);

}
