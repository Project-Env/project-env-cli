package io.projectenv.core.toolsupport.jdk.download.impl.discoapi;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.projectenv.core.commons.process.ProcessOutput;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SimpleDiscoApiClient implements DiscoApiClient {

    private static final String DISCO_API_BASE_URL = "https://api.foojay.io/disco/v2.0/";
    private static final String DISCO_API_DISTRIBUTIONS_URL = DISCO_API_BASE_URL + "distributions?include_versions={0}&include_synonyms={1}";
    private static final String DISCO_API_JDK_PACKAGES_URL = DISCO_API_BASE_URL + "packages/jdks?version={0}&distro={1}&architecture={2}&libc_type={3}&archive_type={4}&operating_system={5}";
    private static final String DISCO_API_JDK_PACKAGE_DETAIL_URL = DISCO_API_BASE_URL + "ephemeral_ids/{0}";

    private static final Duration FIVE_MINUTES = Duration.ofMinutes(5);

    @Override
    public DiscoApiResult<List<DiscoApiDistribution>> getDistributions(boolean includeVersions, boolean includeSynonyms) throws IOException {
        return callApi(formatUrl(DISCO_API_DISTRIBUTIONS_URL, includeVersions, includeSynonyms), new TypeToken<DiscoApiResult<List<DiscoApiDistribution>>>() {
        }.getType());
    }

    public DiscoApiResult<List<DiscoApiJdkPackage>> getJdkPackages(String version, String distro, String architecture, String libCType, String archiveType, String operatingSystem) throws IOException {
        return callApi(formatUrl(DISCO_API_JDK_PACKAGES_URL, version, distro, architecture, libCType, archiveType, operatingSystem), new TypeToken<DiscoApiResult<List<DiscoApiJdkPackage>>>() {
        }.getType());
    }

    public DiscoApiResult<List<DiscoApiJdkPackageDetails>> getJdkPackageDetails(String ephemeralId) throws IOException {
        return callApi(formatUrl(DISCO_API_JDK_PACKAGE_DETAIL_URL, ephemeralId), new TypeToken<DiscoApiResult<List<DiscoApiJdkPackageDetails>>>() {
        }.getType());
    }

    private String formatUrl(String url, Object... parameters) {
        String[] encodedParameters = Arrays.stream(parameters)
                .map(Objects::toString)
                .map(parameter -> URLEncoder.encode(parameter, StandardCharsets.UTF_8))
                .toArray(String[]::new);

        return MessageFormat.format(url, (Object[]) encodedParameters);
    }

    private <T> T callApi(String uri, Type responseType) throws IOException {
        try {
            ProcessOutput.writeDebugMessage("calling DiscoAPI with URL {0}", uri);

            var httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(uri))
                    .header("Accept", "application/json")
                    .timeout(FIVE_MINUTES)
                    .GET()
                    .build();

            HttpResponse<InputStream> response = createHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
            if (response.statusCode() != 200) {
                throw new IOException("Received a non expected status code " + response.statusCode() + " from Disco API with URL " + uri);
            }

            try (Reader reader = new InputStreamReader(response.body())) {
                return createGson().fromJson(reader, responseType);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new IOException(e);
        }
    }

    private HttpClient createHttpClient() {
        return HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(FIVE_MINUTES)
                .build();
    }

    private static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapterFactory(new GsonAdaptersDiscoApiResult())
                .registerTypeAdapterFactory(new GsonAdaptersDiscoApiJdkPackage())
                .registerTypeAdapterFactory(new GsonAdaptersDiscoApiJdkPackageDetails())
                .registerTypeAdapterFactory(new GsonAdaptersDiscoApiDistribution())
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

}
