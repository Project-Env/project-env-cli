package io.projectenv.core.cli.http;

import io.projectenv.core.toolsupport.spi.http.HttpClientProvider;

import java.net.ProxySelector;
import java.net.http.HttpClient;

public class DefaultHttpClientProvider implements HttpClientProvider {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .proxy(ProxySelector.getDefault())
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    @Override
    public HttpClient getHttpClient() {
        return httpClient;
    }

}

