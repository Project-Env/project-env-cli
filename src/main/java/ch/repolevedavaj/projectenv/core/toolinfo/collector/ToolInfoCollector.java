package ch.repolevedavaj.projectenv.core.toolinfo.collector;

import ch.repolevedavaj.projectenv.core.configuration.ToolConfiguration;
import ch.repolevedavaj.projectenv.core.toolinfo.ToolInfo;

import java.io.File;

public interface ToolInfoCollector<ToolConfigurationType extends ToolConfiguration, ToolInfoType extends ToolInfo> {

    ToolInfoType collectToolInfo(ToolConfigurationType toolConfiguration, File toolBinariesDirectory) throws Exception;

    boolean supportsTool(ToolConfiguration toolConfiguration);

}
