package io.projectenv.core.toolsupport.jdk.download;

import io.projectenv.core.commons.process.ProcessOutput;
import io.projectenv.core.commons.system.OperatingSystem;
import io.projectenv.core.toolsupport.jdk.ImmutableJdkConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

class JdkDownloadUrlResolverIT {

    private final JdkDownloadUrlResolver jdkDownloadUrlResolver = JdkDownloadUrlResolverFactory.createJdkDownloadUrlResolver();

    @BeforeAll
    static void activateProcessOutputDebugMode() {
        ProcessOutput.activateDebugMode();
    }

    @Test
    void testGraalVM11() throws IOException, JdkDownloadUrlResolverException {
        assertResolveUrlReturnsValidUrlForAllOS("GraalVM CE 11", "21.1.0");
    }

    @Test
    void testGraalVM16() throws IOException, JdkDownloadUrlResolverException {
        assertResolveUrlReturnsValidUrlForAllOS("GraalVM CE 16", "21.1.0");
    }

    @Test
    void testTemurin8() throws IOException, JdkDownloadUrlResolverException {
        assertResolveUrlReturnsValidUrlForAllOS("Temurin", "8.0.302+8");
    }

    @Test
    void testTemuri11() throws IOException, JdkDownloadUrlResolverException {
        assertResolveUrlReturnsValidUrlForAllOS("Temurin", "11.0.12+7");
    }

    @Test
    void testTemurin16() throws IOException, JdkDownloadUrlResolverException {
        assertResolveUrlReturnsValidUrlForAllOS("Temurin", "16.0.2+7");
    }

    @Test
    void testTemurin17() throws IOException, JdkDownloadUrlResolverException {
        assertResolveUrlReturnsValidUrlForAllOS("Temurin", "17+35");
    }

    private void assertResolveUrlReturnsValidUrlForAllOS(String distribution, String distributionVersion) throws IOException, JdkDownloadUrlResolverException {
        for (OperatingSystem operatingSystem : OperatingSystem.values()) {
            try (MockedStatic<OperatingSystem> mock = Mockito.mockStatic(OperatingSystem.class)) {
                mock.when(OperatingSystem::getCurrentOperatingSystem).thenReturn(operatingSystem);

                var configuration = ImmutableJdkConfiguration.builder()
                        .distribution(distribution)
                        .distributionVersion(distributionVersion)
                        .build();

                var rawUrl = jdkDownloadUrlResolver.resolveUrl(configuration);
                assertThat(rawUrl).isNotNull();
                System.out.println("URL for distribution " + distribution + " with version " + distributionVersion + " and OS " + operatingSystem + ": " + rawUrl);

                var url = new URL(rawUrl);
                var con = (HttpURLConnection) url.openConnection();
                assertThat(con.getResponseCode())
                        .as("config: %s; url: %s; os: %s", configuration.toString(), rawUrl, operatingSystem)
                        .isEqualTo(200);
            }
        }
    }

}