package io.projectenv.core.cli.common;

import io.projectenv.core.toolsupport.spi.ToolSupport;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

public final class ToolSupportHelper {

    private ToolSupportHelper() {
        // noop
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> getToolSupportConfigurationClass(ToolSupport<T> toolSupport) {
        return (Class<T>) Arrays.stream(toolSupport.getClass().getGenericInterfaces())
                .filter(ParameterizedType.class::isInstance)
                .map(ParameterizedType.class::cast)
                .filter(type -> type.getRawType() == ToolSupport.class)
                .flatMap(type -> Arrays.stream(type.getActualTypeArguments()))
                .findFirst()
                .orElseThrow();
    }

}
