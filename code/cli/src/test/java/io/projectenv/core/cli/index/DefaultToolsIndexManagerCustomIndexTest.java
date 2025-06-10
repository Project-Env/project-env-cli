package io.projectenv.core.cli.index;

import io.projectenv.core.cli.http.DefaultHttpClientProvider;
import io.projectenv.core.commons.system.TestEnvironmentVariables;
import io.projectenv.core.toolsupport.spi.index.ToolsIndexManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultToolsIndexManagerCustomIndexTest {

    @TempDir
    static File tempDir;

    @Test
    void resolveMavenDistributionUrl() throws Exception {
        var url = getClass().getResource("custom-index.json").toString();
        try (var ignored = TestEnvironmentVariables.overlayEnv(Map.of("PROJECT_ENV_TOOL_INDEX_V2", url))) {
            ToolsIndexManager manager = new DefaultToolsIndexManager(tempDir, new DefaultHttpClientProvider());
            assertThat(manager.resolveMavenDistributionUrl("customVersion")).isEqualTo("customVersionUrl");
        }
    }

}
