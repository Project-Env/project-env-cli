package io.projectenv.core.cli.index;

import io.projectenv.core.toolsupport.spi.index.ToolsIndexManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultToolsIndexManagerTest {

    @TempDir
    static File tempDir;

    static ToolsIndexManager manager;

    @BeforeAll
    static void setupToolsIndexManager() {
        manager = new DefaultToolsIndexManager(tempDir);
    }

    @Test
    void resolveMavenDistributionUrl() {
        assertThat(manager.resolveMavenDistributionUrl("3.8.5")).isNotEmpty();
    }

    @Test
    void resolveGradleDistributionUrl() {
        assertThat(manager.resolveGradleDistributionUrl("7.4.2")).isNotEmpty();
    }

    @Test
    void resolveNodeJsDistributionUrl() {
        assertThat(manager.resolveNodeJsDistributionUrl("18.2.0")).isNotEmpty();
    }

    @Test
    void resolveJdkDistributionUrl() {
        assertThat(manager.resolveJdkDistributionUrl("temurin", "18.0.1+10")).isNotEmpty();
    }

}