package io.projectenv.core.toolsupport.jdk.download.impl.discoapi;

import java.time.Duration;

public final class DiscoApiClientFactory {

    private DiscoApiClientFactory() {
        // noop
    }

    public static DiscoApiClient createDiscoApiClient() {
        return RetryingProxy.wrap(new SimpleDiscoApiClient(), 10, Duration.ofSeconds(1));
    }

}
