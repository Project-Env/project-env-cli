package io.projectenv.toolsupport.jdk.download.impl;

import io.projectenv.toolsupport.commons.download.DownloadUrlSubstitutorFactory;
import io.projectenv.toolsupport.commons.download.ImmutableDownloadUrlDictionary;
import io.projectenv.toolsupport.commons.system.CPUArchitecture;
import io.projectenv.toolsupport.commons.system.OperatingSystem;
import io.projectenv.toolsupport.jdk.JdkConfiguration;
import io.projectenv.toolsupport.jdk.download.JdkDownloadUrlResolverStrategy;

import java.util.Map;

public class AdoptOpenJdkDownloadUrlResolverStrategy implements JdkDownloadUrlResolverStrategy {

    @Override
    public String resolveUrl(JdkConfiguration jdkConfiguration) {
        var dictionary = ImmutableDownloadUrlDictionary.builder()
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

        return DownloadUrlSubstitutorFactory
                .createDownloadUrlVariableSubstitutor(dictionary)
                .replace("https://github.com/AdoptOpenJDK/openjdk${JAVA_VERSION}-binaries/releases/download/${PATH_DISTRIBUTION_VERSION}/OpenJDK${JAVA_VERSION}U-jdk_${CPU_ARCH}_${OS}_hotspot_${FILE_DISTRIBUTION_VERSION}.${FILE_EXT}");
    }

    private String createPathDistributionVersion(JdkConfiguration jdkConfiguration) {
        return jdkConfiguration.getDistributionVersion();
    }

    private String createFileDistributionVersion(JdkConfiguration jdkConfiguration) {
        return jdkConfiguration.getDistributionVersion()
                .replace("-", "")
                .replace("+", "_");
    }

}
