package io.projectenv.core.toolsupport.jdk.download;

import io.projectenv.core.toolsupport.jdk.JdkConfiguration;

public interface JdkDownloadUrlResolver {
    String resolveUrl(JdkConfiguration jdkConfiguration) throws JdkDownloadUrlResolverException;

}
