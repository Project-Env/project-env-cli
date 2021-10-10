package io.projectenv.core.toolsupport.jdk.download.impl;

import io.projectenv.core.commons.system.CPUArchitecture;
import io.projectenv.core.commons.system.OperatingSystem;
import io.projectenv.core.toolsupport.jdk.ImmutableJdkConfiguration;
import io.projectenv.core.toolsupport.jdk.download.JdkDownloadUrlResolverException;
import io.projectenv.core.toolsupport.jdk.download.impl.discoapi.DiscoApiClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

class DiscoApiJdkDownloadUrlResolverTest {

    @Test
    void testMaxRetries() throws Exception {
        try (MockedStatic<OperatingSystem> operationSystemMock = Mockito.mockStatic(OperatingSystem.class)) {
            operationSystemMock.when(OperatingSystem::getCurrentOperatingSystem).thenReturn(OperatingSystem.LINUX);

            try (MockedStatic<CPUArchitecture> cpuArchitectureMock = Mockito.mockStatic(CPUArchitecture.class)) {
                cpuArchitectureMock.when(CPUArchitecture::getCurrentCPUArchitecture).thenReturn(CPUArchitecture.X64);

                var discoApiClientMock = mock(DiscoApiClient.class);
                when(discoApiClientMock.getJdkPackages("version", "distro", "x64", "tar.gz", "linux"))
                        .thenThrow(new RuntimeException());

                var jdkDownloadUrlResolver = new DiscoApiJdkDownloadUrlResolver(discoApiClientMock);

                Assertions.assertThatExceptionOfType(JdkDownloadUrlResolverException.class)
                        .isThrownBy(() ->
                                jdkDownloadUrlResolver.resolveUrl(ImmutableJdkConfiguration.builder()
                                        .distribution("distro")
                                        .distributionVersion("version")
                                        .addPostExtractionCommands("version")
                                        .build())
                        );

                verify(discoApiClientMock, times(3))
                        .getJdkPackages("version", "distro", "x64", "tar.gz", "linux");
            }
        }
    }

}