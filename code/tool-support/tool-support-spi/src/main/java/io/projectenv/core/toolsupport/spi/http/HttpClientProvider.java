package io.projectenv.core.toolsupport.spi.http;

import java.net.http.HttpClient;

public interface HttpClientProvider {
    HttpClient getHttpClient();
}

