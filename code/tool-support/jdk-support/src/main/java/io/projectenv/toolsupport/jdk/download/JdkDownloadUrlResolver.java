package io.projectenv.toolsupport.jdk.download;

import io.projectenv.toolsupport.jdk.JdkConfiguration;
import io.projectenv.toolsupport.jdk.JdkConfiguration.JdkDistribution;
import io.projectenv.toolsupport.jdk.download.impl.AdoptOpenJdkDownloadUrlResolverStrategy;
import io.projectenv.toolsupport.jdk.download.impl.GraalVmDownloadUrlResolverStrategy;

import java.util.Map;

public final class JdkDownloadUrlResolver {

    private static final Map<JdkDistribution, JdkDownloadUrlResolverStrategy> STRATEGIES = Map.of(
            JdkDistribution.ADOPTOPENJDK, new AdoptOpenJdkDownloadUrlResolverStrategy(),
            JdkDistribution.GRAALVM, new GraalVmDownloadUrlResolverStrategy()
    );

    private JdkDownloadUrlResolver() {
        // noop
    }

    public static String resolveUrl(JdkConfiguration jdkConfiguration) {
        return STRATEGIES.get(jdkConfiguration.getDistribution()).resolveUrl(jdkConfiguration);
    }

}
