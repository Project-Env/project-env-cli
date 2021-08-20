package io.projectenv.core.toolsupport.jdk.download.impl;

import io.projectenv.core.commons.download.DownloadUrlDictionary;
import io.projectenv.core.commons.download.ImmutableDownloadUrlDictionary;
import io.projectenv.core.commons.system.CPUArchitecture;
import io.projectenv.core.commons.system.OperatingSystem;
import io.projectenv.core.toolsupport.jdk.JdkConfiguration;

import java.text.MessageFormat;
import java.util.Map;

public class AdoptOpenJdkDownloadUrlResolverStrategy extends AbstractJdkDownloadUrlResolverStrategy {

    private static final Map<String, String> VERSION_SPECIFIC_PATH_DISTRIBUTION_VERSION_PATTERN = Map.of(
            "8", "jdk{0}"
    );

    private static final String DEFAULT_PATH_DISTRIBUTION_VERSION_PATTERN = "jdk-{0}";

    @Override
    protected String getDownloadUrlPattern(JdkConfiguration jdkConfiguration) {
        return "https://github.com/AdoptOpenJDK/openjdk${JAVA_VERSION}-binaries/releases/download/${PATH_DISTRIBUTION_VERSION}/OpenJDK${JAVA_VERSION}U-jdk_${CPU_ARCH}_${OS}_hotspot_${FILE_DISTRIBUTION_VERSION}.${FILE_EXT}";
    }

    @Override
    protected DownloadUrlDictionary getDownloadUrlDictionary(JdkConfiguration jdkConfiguration) {
        return ImmutableDownloadUrlDictionary.builder()
                .putParameters("JAVA_VERSION", jdkConfiguration.getJavaVersion())
                .putParameters("PATH_DISTRIBUTION_VERSION", createPathDistributionVersion(jdkConfiguration))
                .putParameters("FILE_DISTRIBUTION_VERSION", createFileDistributionVersion(jdkConfiguration))
                .putOperatingSystemSpecificParameters("OS", Map.of(
                        OperatingSystem.MACOS, "mac",
                        OperatingSystem.LINUX, "linux",
                        OperatingSystem.WINDOWS, "windows"
                ))
                .putOperatingSystemSpecificParameters("FILE_EXT", Map.of(
                        OperatingSystem.MACOS, "tar.gz",
                        OperatingSystem.LINUX, "tar.gz",
                        OperatingSystem.WINDOWS, "zip"
                ))
                .putCPUArchitectureSpecificParameters("CPU_ARCH", Map.of(
                        CPUArchitecture.X64, "x64"
                ))
                .build();
    }
    
    private String createPathDistributionVersion(JdkConfiguration jdkConfiguration) {
        var pattern = VERSION_SPECIFIC_PATH_DISTRIBUTION_VERSION_PATTERN
                .getOrDefault(jdkConfiguration.getJavaVersion(), DEFAULT_PATH_DISTRIBUTION_VERSION_PATTERN);

        return MessageFormat.format(pattern, jdkConfiguration.getDistributionVersion());
    }

    private String createFileDistributionVersion(JdkConfiguration jdkConfiguration) {
        return jdkConfiguration.getDistributionVersion()
                .replace("-", "")
                .replace("+", "_");
    }

}
