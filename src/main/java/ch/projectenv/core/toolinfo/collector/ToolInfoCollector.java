package ch.projectenv.core.toolinfo.collector;

import ch.projectenv.core.toolinfo.ToolInfo;
import ch.projectenv.core.configuration.ToolConfiguration;

import java.io.File;

public interface ToolInfoCollector<ToolConfigurationType extends ToolConfiguration, ToolInfoType extends ToolInfo> {

    ToolInfoType collectToolInfo(ToolConfigurationType toolConfiguration, ToolInfoCollectorContext context) throws Exception;

    boolean supportsTool(ToolConfiguration toolConfiguration);

}
