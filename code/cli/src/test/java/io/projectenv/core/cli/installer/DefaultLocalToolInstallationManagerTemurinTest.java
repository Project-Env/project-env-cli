package io.projectenv.core.cli.installer;

import io.projectenv.core.toolsupport.jdk.ImmutableJdkConfiguration;
import io.projectenv.core.toolsupport.jdk.JdkSupport;
import io.projectenv.core.toolsupport.spi.ImmutableToolSupportContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultLocalToolInstallationManagerTemurinTest {

    @Test
    void testTemurinInstallation(@TempDir File projectRoot) throws Exception {
        var installationManager = new DefaultLocalToolInstallationManager(new File(projectRoot, ".tools"));

        var configuration = ImmutableJdkConfiguration.builder()
                .distribution("Temurin")
                .distributionVersion("11.0.12+7")
                .build();

        var context = ImmutableToolSupportContext
                .builder()
                .projectRoot(projectRoot)
                .localToolInstallationManager(installationManager)
                .build();

        var toolInfo = new JdkSupport().prepareTool(configuration, context);
        assertThat(toolInfo).isNotNull();
    }

}
