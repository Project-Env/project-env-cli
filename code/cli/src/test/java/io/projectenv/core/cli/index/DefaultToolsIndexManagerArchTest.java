package io.projectenv.core.cli.index;

import io.projectenv.core.cli.http.DefaultHttpClientProvider;
import io.projectenv.core.commons.system.TestEnvironmentVariables;
import io.projectenv.core.toolsupport.spi.index.ToolsIndexException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultToolsIndexManagerArchTest {

    @TempDir
    File tempDir;

    @Test
    void resolveNodeJsDistributionUrlThrowsForUnknownVersion() throws Exception {
        var url = getClass().getResource("arch-test-index.json").toString();
        try (var ignored = TestEnvironmentVariables.overlayEnv(Map.of("PROJECT_ENV_TOOL_INDEX_V2", url))) {
            var manager = new DefaultToolsIndexManager(tempDir, new DefaultHttpClientProvider());
            assertThatThrownBy(() -> manager.resolveNodeJsDistributionUrl("99.99.99"))
                    .isInstanceOf(ToolsIndexException.class)
                    .hasMessageContaining("Failed to resolve NodeJS 99.99.99 from tool index");
        }
    }

    @Test
    void resolveJdkDistributionUrlThrowsForUnknownVersion() throws Exception {
        var url = getClass().getResource("arch-test-index.json").toString();
        try (var ignored = TestEnvironmentVariables.overlayEnv(Map.of("PROJECT_ENV_TOOL_INDEX_V2", url))) {
            var manager = new DefaultToolsIndexManager(tempDir, new DefaultHttpClientProvider());
            assertThatThrownBy(() -> manager.resolveJdkDistributionUrl("temurin", "99.99.99"))
                    .isInstanceOf(ToolsIndexException.class)
                    .hasMessageContaining("Failed to resolve temurin 99.99.99 from tool index");
        }
    }

    @Test
    void resolveJdkDistributionUrlThrowsForUnknownDistribution() throws Exception {
        var url = getClass().getResource("arch-test-index.json").toString();
        try (var ignored = TestEnvironmentVariables.overlayEnv(Map.of("PROJECT_ENV_TOOL_INDEX_V2", url))) {
            var manager = new DefaultToolsIndexManager(tempDir, new DefaultHttpClientProvider());
            assertThatThrownBy(() -> manager.resolveJdkDistributionUrl("unknown-distro", "21.0.1+12"))
                    .isInstanceOf(ToolsIndexException.class)
                    .hasMessageContaining("Failed to resolve unknown-distro 21.0.1+12 from tool index");
        }
    }

    @Test
    void resolveNodeJsDistributionUrlReturnsUrlForAvailableOsAndArch() throws Exception {
        var url = getClass().getResource("arch-test-index.json").toString();
        try (var ignored = TestEnvironmentVariables.overlayEnv(Map.of("PROJECT_ENV_TOOL_INDEX_V2", url))) {
            var manager = new DefaultToolsIndexManager(tempDir, new DefaultHttpClientProvider());
            // This test will pass on any platform that has a matching entry in the test index.
            // On macOS amd64/aarch64 and linux amd64, the resolution should succeed.
            var result = manager.resolveNodeJsDistributionUrl("20.0.0");
            assertThat(result).isNotEmpty();
        }
    }

    @Test
    void resolveJdkDistributionUrlReturnsUrlForAvailableOsAndArch() throws Exception {
        var url = getClass().getResource("arch-test-index.json").toString();
        try (var ignored = TestEnvironmentVariables.overlayEnv(Map.of("PROJECT_ENV_TOOL_INDEX_V2", url))) {
            var manager = new DefaultToolsIndexManager(tempDir, new DefaultHttpClientProvider());
            var result = manager.resolveJdkDistributionUrl("temurin", "21.0.1+12");
            assertThat(result).isNotEmpty();
        }
    }

}
