package io.projectenv.core.cli;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VersionProviderTest {

    @Test
    void getVersion_shouldReturnNonNullVersion() {
        String version = VersionProvider.getVersion();

        assertThat(version).isNotNull();
        assertThat(version).isNotEmpty();
        assertThat(version).doesNotContain("$");
    }

}

