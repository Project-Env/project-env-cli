package io.projectenv.core.cli.common;

import io.projectenv.core.cli.ToolSupportHelper;
import io.projectenv.core.toolsupport.spi.ToolInfo;
import io.projectenv.core.toolsupport.spi.ToolSupport;
import io.projectenv.core.toolsupport.spi.ToolSupportContext;
import io.projectenv.core.toolsupport.spi.ToolSupportException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ToolSupportHelperTest {

    @Test
    void getToolSupportConfigurationClassTest() {
        var toolSupport = new TestToolSupport();

        assertThat(ToolSupportHelper.getToolSupportConfigurationClass(toolSupport))
                .isEqualTo(TestToolConfiguration.class);
    }

    private static class TestToolSupport implements ToolSupport<TestToolConfiguration> {

        @Override
        public String getToolIdentifier() {
            return null;
        }

        @Override
        public Class<TestToolConfiguration> getToolConfigurationClass() {
            return TestToolConfiguration.class;
        }

        @Override
        public ToolInfo prepareTool(TestToolConfiguration toolConfiguration, ToolSupportContext context) throws ToolSupportException {
            return null;
        }

    }

    private static class TestToolConfiguration {

    }

}