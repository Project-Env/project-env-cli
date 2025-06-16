package io.projectenv.core.cli.http;

import io.projectenv.core.commons.process.ProcessOutput;
import io.projectenv.core.toolsupport.spi.http.HttpClientProvider;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.net.http.HttpClient;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class DefaultHttpClientProvider implements HttpClientProvider {

    private final HttpClient httpClient;

    public DefaultHttpClientProvider() {
        HttpClient.Builder builder = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL);

        ProxySelector proxySelector = createProxySelectorFromEnv();
        builder.proxy(proxySelector != null ? proxySelector : ProxySelector.getDefault());

        SSLContext sslContext = createCodexAwareSslContext();
        if (sslContext != null) {
            builder.sslContext(sslContext);
        }

        this.httpClient = builder.build();
    }

    /**
     * Returns the configured HttpClient instance.
     */
    @Override
    public HttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * Creates a ProxySelector based on environment variables.
     */
    private ProxySelector createProxySelectorFromEnv() {
        String httpProxy = System.getenv("http_proxy");
        String httpsProxy = System.getenv("https_proxy");
        String noProxy = System.getenv("no_proxy");
        if (httpProxy == null && httpsProxy == null) {
            return null;
        }
        return new EnvProxySelector(httpProxy, httpsProxy, noProxy);
    }

    /**
     * Creates an SSLContext that trusts both the default system CAs and the Codex proxy certificate, if present.
     */
    private SSLContext createCodexAwareSslContext() {
        String certPath = System.getenv("CODEX_PROXY_CERT");
        if (certPath == null) {
            return null;
        }
        try {
            X509Certificate codexCert = loadCertificate(certPath);
            TrustManagerFactory defaultTmf = createDefaultTrustManagerFactory();
            TrustManagerFactory codexTmf = createTrustManagerFactoryWithCert(codexCert);
            javax.net.ssl.TrustManager[] combined =
                    Stream.concat(
                            Arrays.stream(defaultTmf.getTrustManagers()),
                            Arrays.stream(codexTmf.getTrustManagers())
                    ).toArray(javax.net.ssl.TrustManager[]::new);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, combined, null);
            return sslContext;
        } catch (Exception e) {
            ProcessOutput.writeDebugMessage("Failed to load Codex proxy certificate: {0}", e.getMessage());
            ProcessOutput.writeDebugMessage(e);
            return null;
        }
    }

    /**
     * Loads an X.509 certificate from the given file path.
     */
    private X509Certificate loadCertificate(String certPath) throws Exception {
        try (InputStream certInput = new FileInputStream(certPath)) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return (X509Certificate) cf.generateCertificate(certInput);
        }
    }

    /**
     * Returns the default system TrustManagerFactory.
     */
    private TrustManagerFactory createDefaultTrustManagerFactory() throws Exception {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init((KeyStore) null);
        return tmf;
    }

    /**
     * Returns a TrustManagerFactory initialized with the given certificate.
     */
    private TrustManagerFactory createTrustManagerFactoryWithCert(X509Certificate cert) throws Exception {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        ks.setCertificateEntry("codex-proxy-cert", cert);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);
        return tmf;
    }

    /**
     * Custom ProxySelector that uses http_proxy/https_proxy/no_proxy env vars
     */
    private static class EnvProxySelector extends ProxySelector {
        private final URI httpProxyUri;
        private final URI httpsProxyUri;
        private final List<String> noProxyHosts;

        EnvProxySelector(String httpProxy, String httpsProxy, String noProxy) {
            this.httpProxyUri = parseProxyUri(httpProxy);
            this.httpsProxyUri = parseProxyUri(httpsProxy);
            this.noProxyHosts = parseNoProxy(noProxy);
        }

        @Override
        public List<Proxy> select(URI uri) {
            if (uri == null) throw new IllegalArgumentException("URI can't be null");
            String host = uri.getHost();
            if (host != null && isNoProxy(host)) {
                return List.of(Proxy.NO_PROXY);
            }
            String scheme = uri.getScheme();
            if ("http".equalsIgnoreCase(scheme) && httpProxyUri != null) {
                return List.of(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(httpProxyUri.getHost(), httpProxyUri.getPort())));
            }
            if ("https".equalsIgnoreCase(scheme) && httpsProxyUri != null) {
                return List.of(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(httpsProxyUri.getHost(), httpsProxyUri.getPort())));
            }
            return List.of(Proxy.NO_PROXY);
        }

        @Override
        public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
            ProcessOutput.writeDebugMessage("Proxy connection failed for URI: {0}, address: {1}, error: {2}", uri, sa, ioe.getMessage());
            ProcessOutput.writeDebugMessage(ioe);
        }

        /**
         * Parses a proxy URI from a string, adding scheme if missing.
         */
        private static URI parseProxyUri(String proxy) {
            if (proxy == null || proxy.isEmpty()) return null;
            try {
                if (!proxy.contains("://")) {
                    proxy = "http://" + proxy;
                }
                return new URI(proxy);
            } catch (URISyntaxException e) {
                ProcessOutput.writeDebugMessage("Malformed proxy URI " + proxy + " - skipping proxy configuration...");
                ProcessOutput.writeDebugMessage(e);
                return null;
            }
        }

        /**
         * Parses the no_proxy environment variable into a list.
         */
        private static List<String> parseNoProxy(String noProxy) {
            if (noProxy == null || noProxy.isEmpty()) return List.of();
            return Arrays.asList(noProxy.split(","));
        }

        /**
         * Checks if the given host matches any no_proxy entry.
         */
        private boolean isNoProxy(String host) {
            for (String entry : noProxyHosts) {
                String trimmed = entry.trim();
                if (trimmed.isEmpty()) continue;
                if (host.endsWith(trimmed)) {
                    return true;
                }
            }
            return false;
        }
    }
}
