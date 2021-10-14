package io.projectenv.core.toolsupport.jdk.download.impl.discoapi;

public final class DiscoApiClientFactory {

    private DiscoApiClientFactory() {
        // noop
    }

    public static DiscoApiClient createDiscoApiClient() {
        return new SimpleDiscoApiClient();
    }

}
