package io.projectenv.core.toolsupport.jdk.download;

import io.projectenv.core.toolsupport.jdk.download.impl.DiscoApiJdkDownloadUrlResolver;
import io.projectenv.core.toolsupport.jdk.download.impl.discoapi.DiscoApiClientFactory;

public final class JdkDownloadUrlResolverFactory {

    private JdkDownloadUrlResolverFactory() {
        // noop
    }

    public static JdkDownloadUrlResolver createJdkDownloadUrlResolver() {
        return new DiscoApiJdkDownloadUrlResolver(DiscoApiClientFactory.createDiscoApiClient());
    }


}
