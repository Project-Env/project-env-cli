package io.projectenv.core.toolsupport.jdk.download;

import io.projectenv.core.toolsupport.jdk.JdkConfiguration;

public interface JdkDownloadUrlResolverStrategy {

    String resolveUrl(JdkConfiguration jdkConfiguration);

}
