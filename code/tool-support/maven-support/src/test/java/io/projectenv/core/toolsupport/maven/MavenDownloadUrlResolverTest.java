package io.projectenv.core.toolsupport.maven;

import io.projectenv.core.commons.system.OperatingSystem;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

class MavenDownloadUrlResolverTest {

    @Test
    void test381() throws IOException {
        assertResolveUrlReturnsValidUrlForAllOS("3.8.1");
    }

    @Test
    void test363() throws IOException {
        assertResolveUrlReturnsValidUrlForAllOS("3.6.3");
    }

    private void assertResolveUrlReturnsValidUrlForAllOS(String version) throws IOException {
        for (OperatingSystem operatingSystem : OperatingSystem.values()) {
            try (MockedStatic<OperatingSystem> mock = Mockito.mockStatic(OperatingSystem.class)) {
                mock.when(OperatingSystem::getCurrentOperatingSystem).thenReturn(operatingSystem);

                var rawUrl = MavenDownloadUrlResolver.resolveUrl(version);

                var url = new URL(rawUrl);
                var con = (HttpURLConnection) url.openConnection();
                assertThat(con.getResponseCode())
                        .as("version: %s; url: %s; os: %s", version, rawUrl, operatingSystem)
                        .isEqualTo(200);
            }
        }
    }

}