package ch.repolevedavaj.projectenv.core.toolinfo.collector;

import ch.repolevedavaj.projectenv.core.configuration.GenericToolConfiguration;
import ch.repolevedavaj.projectenv.core.toolinfo.GenericToolInfo;
import ch.repolevedavaj.projectenv.core.toolinfo.ImmutableGenericToolInfo;
import ch.repolevedavaj.projectenv.core.toolinfo.ToolInfo;

public class GenericToolInfoCollector extends AbstractToolInfoCollector<GenericToolConfiguration, GenericToolInfo> {

    @Override
    protected Class<GenericToolConfiguration> getToolConfigurationClass() {
        return GenericToolConfiguration.class;
    }

    @Override
    protected GenericToolInfo collectToolSpecificInfo(ToolInfo baseToolInfo, GenericToolConfiguration toolConfiguration) {
        return ImmutableGenericToolInfo
                .builder()
                .from(baseToolInfo)
                .build();
    }

}
