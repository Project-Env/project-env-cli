package io.projectenv.core.toolsupport.jdk.download;

import io.projectenv.core.commons.system.OperatingSystem;
import io.projectenv.core.toolsupport.jdk.ImmutableJdkConfiguration;
import io.projectenv.core.toolsupport.jdk.JdkConfiguration.JdkDistribution;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

class JdkDownloadUrlResolverTest {

    @Test
    void testGraalVMVersion11() throws IOException {
        assertResolveUrlReturnsValidUrlForAllOS(JdkDistribution.GRAALVM, "11", "21.1.0");
    }

    @Test
    void testGraalVMVersion16() throws IOException {
        assertResolveUrlReturnsValidUrlForAllOS(JdkDistribution.GRAALVM, "16", "21.1.0");
    }

    @Test
    void testAdoptOpenJDKVersion8() throws IOException {
        assertResolveUrlReturnsValidUrlForAllOS(JdkDistribution.ADOPTOPENJDK, "8", "8u292-b10");
    }

    @Test
    void testAdoptOpenJDKVersion11() throws IOException {
        assertResolveUrlReturnsValidUrlForAllOS(JdkDistribution.ADOPTOPENJDK, "11", "11.0.11+9");
    }

    @Test
    void testAdoptOpenJDKVersion16() throws IOException {
        assertResolveUrlReturnsValidUrlForAllOS(JdkDistribution.ADOPTOPENJDK, "16", "16.0.1+9");
    }

    @Test
    void testAdoptiumVersion8() throws IOException {
        assertResolveUrlReturnsValidUrlForAllOS(JdkDistribution.ADOPTIUM, "8", "8u302-b08");
    }

    @Test
    void testAdoptiumVersion11() throws IOException {
        assertResolveUrlReturnsValidUrlForAllOS(JdkDistribution.ADOPTIUM, "11", "11.0.12+7");
    }

    @Test
    void testAdoptiumVersion16() throws IOException {
        assertResolveUrlReturnsValidUrlForAllOS(JdkDistribution.ADOPTIUM, "16", "16.0.2+7");
    }

    private void assertResolveUrlReturnsValidUrlForAllOS(JdkDistribution distribution, String javaVersion, String distributionVersion) throws IOException {
        for (OperatingSystem operatingSystem : OperatingSystem.values()) {
            try (MockedStatic<OperatingSystem> mock = Mockito.mockStatic(OperatingSystem.class)) {
                mock.when(OperatingSystem::getCurrentOperatingSystem).thenReturn(operatingSystem);

                var configuration = ImmutableJdkConfiguration.builder()
                        .javaVersion(javaVersion)
                        .distribution(distribution)
                        .distributionVersion(distributionVersion)
                        .build();

                var rawUrl = JdkDownloadUrlResolver.resolveUrl(configuration);

                var url = new URL(rawUrl);
                var con = (HttpURLConnection) url.openConnection();
                assertThat(con.getResponseCode())
                        .as("config: %s; url: %s; os: %s", configuration.toString(), rawUrl, operatingSystem)
                        .isEqualTo(200);
            }
        }
    }

}