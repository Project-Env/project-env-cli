package io.projectenv.core.toolsupport.jdk.download.impl;

import io.projectenv.core.toolsupport.jdk.JdkConfiguration;

public class AdoptiumDownloadUrlResolverStrategy extends AdoptOpenJdkDownloadUrlResolverStrategy {

    @Override
    protected String getDownloadUrlPattern(JdkConfiguration jdkConfiguration) {
        return "https://github.com/adoptium/temurin${JAVA_VERSION}-binaries/releases/download/${PATH_DISTRIBUTION_VERSION}/OpenJDK${JAVA_VERSION}U-jdk_${CPU_ARCH}_${OS}_hotspot_${FILE_DISTRIBUTION_VERSION}.${FILE_EXT}";
    }

}
