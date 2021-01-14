package ch.repolevedavaj.projectenv.core.toolinfo.collector;

import ch.repolevedavaj.projectenv.core.common.OperatingSystem;
import ch.repolevedavaj.projectenv.core.configuration.JdkConfiguration;
import ch.repolevedavaj.projectenv.core.toolinfo.ImmutableJdkInfo;
import ch.repolevedavaj.projectenv.core.toolinfo.JdkInfo;
import ch.repolevedavaj.projectenv.core.toolinfo.ToolInfo;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

public class JdkInfoCollector extends AbstractToolInfoCollector<JdkConfiguration, JdkInfo> {

    @Override
    protected Class<JdkConfiguration> getToolConfigurationClass() {
        return JdkConfiguration.class;
    }

    @Override
    protected File getRelevantToolBinariesDirectory(File toolBinariesDirectory) {
        File relevantProjectToolRoot = super.getRelevantToolBinariesDirectory(toolBinariesDirectory);
        if (OperatingSystem.getCurrentOS() == OperatingSystem.MACOS) {
            return new File(relevantProjectToolRoot, "Contents/Home");
        }

        return relevantProjectToolRoot;
    }

    @Override
    protected JdkInfo collectToolSpecificInfo(ToolInfo baseToolInfo, JdkConfiguration toolConfiguration) {
        return ImmutableJdkInfo
                .builder()
                .from(baseToolInfo)
                .build();
    }

    @Override
    protected Map<String, String> getAdditionalExports() {
        return Map.of("JAVA_HOME", StringUtils.EMPTY);
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
