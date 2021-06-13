package io.projectenv.toolsupport.jdk.download;

import io.projectenv.toolsupport.jdk.JdkConfiguration;

public interface JdkDownloadUrlResolverStrategy {

    String resolveUrl(JdkConfiguration jdkConfiguration);

}
