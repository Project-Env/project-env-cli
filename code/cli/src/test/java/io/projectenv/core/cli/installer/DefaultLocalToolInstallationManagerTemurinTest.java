package io.projectenv.core.cli.installer;

import io.projectenv.core.cli.http.DefaultHttpClientProvider;
import io.projectenv.core.cli.index.DefaultToolsIndexManager;
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
        var httpClientProvider = new DefaultHttpClientProvider();
        var defaultToolsIndexManager = new DefaultToolsIndexManager(new File(projectRoot, ".tools"), httpClientProvider);

        var configuration = ImmutableJdkConfiguration.builder()
                .distribution("Temurin")
                .version("11.0.12+7")
                .build();

        var context = ImmutableToolSupportContext
                .builder()
                .projectRoot(projectRoot)
                .localToolInstallationManager(installationManager)
                .toolsIndexManager(defaultToolsIndexManager)
                .httpClientProvider(httpClientProvider)
                .build();

        var toolInfo = new JdkSupport().prepareTool(configuration, context);
        assertThat(toolInfo).isNotNull();
    }

}
