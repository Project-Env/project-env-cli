package io.projectenv.core.toolinfo.collector;

import io.projectenv.core.toolinfo.ToolInfo;
import io.projectenv.core.configuration.ToolConfiguration;

public interface ToolInfoCollector<ToolConfigurationType extends ToolConfiguration, ToolInfoType extends ToolInfo> {

    ToolInfoType collectToolInfo(ToolConfigurationType toolConfiguration, ToolInfoCollectorContext context) throws Exception;

    boolean supportsTool(ToolConfiguration toolConfiguration);

}
