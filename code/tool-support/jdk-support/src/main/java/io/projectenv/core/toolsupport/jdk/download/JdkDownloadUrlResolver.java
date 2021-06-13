package io.projectenv.core.toolsupport.jdk.download;

import io.projectenv.core.toolsupport.jdk.JdkConfiguration;
import io.projectenv.core.toolsupport.jdk.download.impl.AdoptOpenJdkDownloadUrlResolverStrategy;
import io.projectenv.core.toolsupport.jdk.download.impl.GraalVmDownloadUrlResolverStrategy;

import java.util.Map;

public final class JdkDownloadUrlResolver {

    private static final Map<JdkConfiguration.JdkDistribution, JdkDownloadUrlResolverStrategy> STRATEGIES = Map.of(
            JdkConfiguration.JdkDistribution.ADOPTOPENJDK, new AdoptOpenJdkDownloadUrlResolverStrategy(),
            JdkConfiguration.JdkDistribution.GRAALVM, new GraalVmDownloadUrlResolverStrategy()
    );

    private JdkDownloadUrlResolver() {
        // noop
    }

    public static String resolveUrl(JdkConfiguration jdkConfiguration) {
        return STRATEGIES.get(jdkConfiguration.getDistribution()).resolveUrl(jdkConfiguration);
    }

}
