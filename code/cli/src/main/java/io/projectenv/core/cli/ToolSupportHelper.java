package io.projectenv.core.cli;

import io.projectenv.core.toolsupport.spi.ToolSupport;

public final class ToolSupportHelper {

    private ToolSupportHelper() {
        // noop
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> getToolSupportConfigurationClass(ToolSupport<T> toolSupport) {
        return toolSupport.getToolConfigurationClass();
    }

}
