package io.projectenv.core.toolsupport.nodejs;

import io.projectenv.core.commons.system.CpuArchitecture;
import io.projectenv.core.commons.system.OperatingSystem;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GenericToolSupportTest {

    private final GenericToolSupport support = new GenericToolSupport();

    @Test
    void isAvailableWithArchSpecificMatch() {
        var config = ImmutableGenericToolConfiguration.builder()
                .addDownloadUrls(ImmutableDownloadUrlConfiguration.builder()
                        .downloadUrl("https://example.com/tool-linux-amd64.tar.gz")
                        .targetOs(OperatingSystem.LINUX)
                        .targetArch(CpuArchitecture.AMD64)
                        .build())
                .addDownloadUrls(ImmutableDownloadUrlConfiguration.builder()
                        .downloadUrl("https://example.com/tool-linux-aarch64.tar.gz")
                        .targetOs(OperatingSystem.LINUX)
                        .targetArch(CpuArchitecture.AARCH64)
                        .build())
                .addDownloadUrls(ImmutableDownloadUrlConfiguration.builder()
                        .downloadUrl("https://example.com/tool-macos-amd64.tar.gz")
                        .targetOs(OperatingSystem.MACOS)
                        .targetArch(CpuArchitecture.AMD64)
                        .build())
                .addDownloadUrls(ImmutableDownloadUrlConfiguration.builder()
                        .downloadUrl("https://example.com/tool-macos-aarch64.tar.gz")
                        .targetOs(OperatingSystem.MACOS)
                        .targetArch(CpuArchitecture.AARCH64)
                        .build())
                .addDownloadUrls(ImmutableDownloadUrlConfiguration.builder()
                        .downloadUrl("https://example.com/tool-windows-amd64.zip")
                        .targetOs(OperatingSystem.WINDOWS)
                        .targetArch(CpuArchitecture.AMD64)
                        .build())
                .build();

        assertThat(support.isAvailable(config)).isTrue();
    }

    @Test
    void isAvailableWithOsOnlyMatch() {
        var config = ImmutableGenericToolConfiguration.builder()
                .addDownloadUrls(ImmutableDownloadUrlConfiguration.builder()
                        .downloadUrl("https://example.com/tool-linux.tar.gz")
                        .targetOs(OperatingSystem.LINUX)
                        .build())
                .addDownloadUrls(ImmutableDownloadUrlConfiguration.builder()
                        .downloadUrl("https://example.com/tool-macos.tar.gz")
                        .targetOs(OperatingSystem.MACOS)
                        .build())
                .addDownloadUrls(ImmutableDownloadUrlConfiguration.builder()
                        .downloadUrl("https://example.com/tool-windows.zip")
                        .targetOs(OperatingSystem.WINDOWS)
                        .build())
                .build();

        assertThat(support.isAvailable(config)).isTrue();
    }

    @Test
    void isAvailableWithFallbackDownloadUrl() {
        var config = ImmutableGenericToolConfiguration.builder()
                .downloadUrl("https://example.com/tool-universal.tar.gz")
                .build();

        assertThat(support.isAvailable(config)).isTrue();
    }

    @Test
    void isNotAvailableWhenNoMatchingOs() {
        // Create config with only a non-matching OS
        var nonCurrentOs = OperatingSystem.getCurrentOperatingSystem() == OperatingSystem.LINUX
                ? OperatingSystem.WINDOWS
                : OperatingSystem.LINUX;

        var config = ImmutableGenericToolConfiguration.builder()
                .addDownloadUrls(ImmutableDownloadUrlConfiguration.builder()
                        .downloadUrl("https://example.com/tool-other.tar.gz")
                        .targetOs(nonCurrentOs)
                        .build())
                .build();

        assertThat(support.isAvailable(config)).isFalse();
    }

    @Test
    void archSpecificMatchTakesPriorityOverOsOnlyMatch() {
        var currentOs = OperatingSystem.getCurrentOperatingSystem();
        var currentArch = CpuArchitecture.getCurrentCpuArchitecture();

        var config = ImmutableGenericToolConfiguration.builder()
                .addDownloadUrls(ImmutableDownloadUrlConfiguration.builder()
                        .downloadUrl("https://example.com/tool-os-only.tar.gz")
                        .targetOs(currentOs)
                        .build())
                .addDownloadUrls(ImmutableDownloadUrlConfiguration.builder()
                        .downloadUrl("https://example.com/tool-arch-specific.tar.gz")
                        .targetOs(currentOs)
                        .targetArch(currentArch)
                        .build())
                .build();

        // Tool should be available
        assertThat(support.isAvailable(config)).isTrue();
    }

}
