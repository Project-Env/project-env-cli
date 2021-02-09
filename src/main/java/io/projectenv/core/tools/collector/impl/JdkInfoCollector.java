package io.projectenv.core.tools.collector.impl;

import io.projectenv.core.common.OperatingSystem;
import io.projectenv.core.configuration.JdkConfiguration;
import io.projectenv.core.tools.collector.ToolInfoCollectorContext;
import io.projectenv.core.tools.info.ImmutableJdkInfo;
import io.projectenv.core.tools.info.JdkInfo;
import io.projectenv.core.tools.info.ToolInfo;

import java.io.File;
import java.util.List;
import java.util.Map;

public class JdkInfoCollector extends AbstractToolInfoCollector<JdkConfiguration, JdkInfo> {

    @Override
    protected Class<JdkConfiguration> getToolConfigurationClass() {
        return JdkConfiguration.class;
    }

    @Override
    protected File getRelevantToolBinariesDirectory(ToolInfoCollectorContext context) {
        File relevantProjectToolRoot = super.getRelevantToolBinariesDirectory(context);
        if (OperatingSystem.getCurrentOS() == OperatingSystem.MACOS) {
            return new File(relevantProjectToolRoot, "Contents/Home");
        }

        return relevantProjectToolRoot;
    }

    @Override
    protected JdkInfo collectToolSpecificInfo(ToolInfo baseToolInfo, JdkConfiguration toolConfiguration, ToolInfoCollectorContext context) {
        return ImmutableJdkInfo
                .builder()
                .from(baseToolInfo)
                .build();
    }

    @Override
    protected Map<String, String> getAdditionalExports() {
        return Map.of("JAVA_HOME", "/");
    }

    @Override
    protected List<String> getAdditionalPathElements() {
        return List.of("/bin");
    }

    @Override
    protected String getPrimaryExecutableName() {
        return "java";
    }

}
