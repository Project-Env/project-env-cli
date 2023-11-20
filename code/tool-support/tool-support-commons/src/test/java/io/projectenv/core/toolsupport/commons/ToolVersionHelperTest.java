package io.projectenv.core.toolsupport.commons;

import io.projectenv.core.toolsupport.spi.UpgradeScope;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ToolVersionHelperTest {

    @Test
    void testNextToolVersionWithJava() {
        Optional<String> nextToolVersion = ToolVersionHelper.getNextToolVersion("11.0.20.1+1", UpgradeScope.MINOR, Set.of("11.0.21+9", "18+36"));
        assertThat(nextToolVersion).contains("11.0.21+9");

        nextToolVersion = ToolVersionHelper.getNextToolVersion("11.0.20.1+1", UpgradeScope.MAJOR, Set.of("11.0.21+9", "18+36"));
        assertThat(nextToolVersion).contains("18+36");
    }

    @Test
    void testNextToolVersionWithSemver() {
        Optional<String> nextToolVersion = ToolVersionHelper.getNextToolVersion("3.0.4", UpgradeScope.PATCH, Set.of("3.0.5", "3.9.5"));
        assertThat(nextToolVersion).contains("3.0.5");

        nextToolVersion = ToolVersionHelper.getNextToolVersion("3.0.4", UpgradeScope.MINOR, Set.of("3.0.5", "3.9.5"));
        assertThat(nextToolVersion).contains("3.9.5");

        nextToolVersion = ToolVersionHelper.getNextToolVersion("3.0.4", UpgradeScope.MAJOR, Set.of("3.0.5", "3.9.5"));
        assertThat(nextToolVersion).contains("3.9.5");
    }

}