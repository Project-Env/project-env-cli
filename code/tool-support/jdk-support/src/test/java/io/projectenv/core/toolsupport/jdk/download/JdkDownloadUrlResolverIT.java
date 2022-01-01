package io.projectenv.core.toolsupport.jdk.download;

import io.projectenv.core.commons.process.ProcessOutput;
import io.projectenv.core.commons.system.OperatingSystem;
import io.projectenv.core.toolsupport.jdk.ImmutableJdkConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class JdkDownloadUrlResolverIT {

    private final JdkDownloadUrlResolver jdkDownloadUrlResolver = JdkDownloadUrlResolverFactory.createJdkDownloadUrlResolver();

    @BeforeAll
    static void activateProcessOutputDebugMode() {
        ProcessOutput.activateDebugMode();
    }

    @ParameterizedTest
    @MethodSource("getTestArguments")
    void testResolveUrlReturnsValidUrlForAllOS(String distribution, String version) throws IOException, JdkDownloadUrlResolverException {
        assertResolveUrlReturnsValidUrlForAllOS(distribution, version);
    }

    private static Stream<Arguments> getTestArguments() {
        return Stream.of(
                // test different names/synonyms for a distribution
                Arguments.of("Graal VM CE 11", "21.1.0"),
                Arguments.of("graalvm_ce11", "21.1.0"),
                Arguments.of("graalvmce11", "21.1.0"),
                Arguments.of("GraalVM CE 11", "21.1.0"),
                Arguments.of("GraalVMCE11", "21.1.0"),
                Arguments.of("GraalVM_CE11", "21.1.0"),
                // test other distributions
                Arguments.of("GraalVM CE 16", "21.1.0"),
                Arguments.of("Temurin", "8.0.302+8"),
                Arguments.of("Temurin", "11.0.12+7"),
                Arguments.of("Temurin", "16.0.2+7"),
                Arguments.of("Temurin", "17.0.1+12")
        );
    }

    private void assertResolveUrlReturnsValidUrlForAllOS(String distribution, String distributionVersion) throws IOException, JdkDownloadUrlResolverException {
        for (OperatingSystem operatingSystem : OperatingSystem.values()) {
            try (MockedStatic<OperatingSystem> mock = Mockito.mockStatic(OperatingSystem.class)) {
                mock.when(OperatingSystem::getCurrentOperatingSystem).thenReturn(operatingSystem);

                var configuration = ImmutableJdkConfiguration.builder()
                        .distribution(distribution)
                        .version(distributionVersion)
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