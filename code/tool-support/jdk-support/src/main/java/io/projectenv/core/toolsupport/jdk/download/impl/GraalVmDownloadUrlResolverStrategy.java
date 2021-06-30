package io.projectenv.core.toolsupport.jdk.download.impl;

import io.projectenv.core.commons.download.DownloadUrlSubstitutorFactory;
import io.projectenv.core.commons.download.ImmutableDownloadUrlDictionary;
import io.projectenv.core.commons.system.CPUArchitecture;
import io.projectenv.core.commons.system.OperatingSystem;
import io.projectenv.core.toolsupport.jdk.JdkConfiguration;
import io.projectenv.core.toolsupport.jdk.download.JdkDownloadUrlResolverStrategy;

import java.util.Map;

public class GraalVmDownloadUrlResolverStrategy implements JdkDownloadUrlResolverStrategy {

    @Override
    public String resolveUrl(JdkConfiguration jdkConfiguration) {
        var dictionary = ImmutableDownloadUrlDictionary.builder()
                .putParameters("JAVA_VERSION", jdkConfiguration.getJavaVersion())
                .putParameters("DISTRIBUTION_VERSION", jdkConfiguration.getDistributionVersion())
                .putOperatingSystemSpecificParameters("OS", Map.of(
                        OperatingSystem.MACOS, "darwin",
                        OperatingSystem.LINUX, "linux",
                        OperatingSystem.WINDOWS, "windows"
                ))
                .putOperatingSystemSpecificParameters("FILE_EXT", Map.of(
                        OperatingSystem.MACOS, "tar.gz",
                        OperatingSystem.LINUX, "tar.gz",
                        OperatingSystem.WINDOWS, "zip"
                ))
                .putCPUArchitectureSpecificParameters("CPU_ARCH", Map.of(
                        CPUArchitecture.X64, "amd64"
                ))
                .build();

        return DownloadUrlSubstitutorFactory
                .createDownloadUrlVariableSubstitutor(dictionary)
                .replace("https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-${DISTRIBUTION_VERSION}/graalvm-ce-java${JAVA_VERSION}-${OS}-${CPU_ARCH}-${DISTRIBUTION_VERSION}.${FILE_EXT}");
    }

}
