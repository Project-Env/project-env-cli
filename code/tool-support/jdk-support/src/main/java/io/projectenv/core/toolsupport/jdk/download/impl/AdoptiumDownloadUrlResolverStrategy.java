package io.projectenv.core.toolsupport.jdk.download.impl;

import io.projectenv.core.toolsupport.jdk.JdkConfiguration;
import org.apache.commons.lang3.math.NumberUtils;

public class AdoptiumDownloadUrlResolverStrategy extends AdoptOpenJdkDownloadUrlResolverStrategy {

    @Override
    protected String getDownloadUrlPattern(JdkConfiguration jdkConfiguration) {
        if (NumberUtils.toInt(jdkConfiguration.getJavaVersion()) >= 17) {
            return "https://github.com/adoptium/temurin${JAVA_VERSION}-binaries/releases/download/${PATH_DISTRIBUTION_VERSION}/OpenJDK${JAVA_VERSION}-jdk_${CPU_ARCH}_${OS}_hotspot_${FILE_DISTRIBUTION_VERSION}.${FILE_EXT}";
        } else {
            return "https://github.com/adoptium/temurin${JAVA_VERSION}-binaries/releases/download/${PATH_DISTRIBUTION_VERSION}/OpenJDK${JAVA_VERSION}U-jdk_${CPU_ARCH}_${OS}_hotspot_${FILE_DISTRIBUTION_VERSION}.${FILE_EXT}";
        }
    }

}
