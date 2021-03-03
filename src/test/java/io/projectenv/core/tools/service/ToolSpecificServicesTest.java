package io.projectenv.core.tools.service;

import io.projectenv.core.tools.service.collector.ToolInfoCollector;
import io.projectenv.core.tools.service.installer.ToolInstaller;
import io.projectenv.core.tools.service.resources.LocalToolResourcesProcessor;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ToolSpecificServicesTest {

    private static final String PROJECT_ENV_PACKAGE_PREFIX = "io.projectenv";

    private static Reflections reflections;

    @BeforeAll
    static void setupReflections() {
        reflections = new Reflections(PROJECT_ENV_PACKAGE_PREFIX);
    }

    @Test
    void testAllToolInfoCollectorsRegistered() throws IOException {
        assertAllServicesRegistered(ToolInfoCollector.class);
    }

    @Test
    void testAllToolInstallersRegistered() throws IOException {
        assertAllServicesRegistered(ToolInstaller.class);
    }

    @Test
    void testAllLocalToolResourcesProcessorsRegistered() throws IOException {
        assertAllServicesRegistered(LocalToolResourcesProcessor.class);
    }

    private void assertAllServicesRegistered(Class<?> serviceClass) throws IOException {
        List<String> availableServices = getAvailableServices(serviceClass);
        List<String> registeredServices = getRegisteredServices(serviceClass);

        assertThat(registeredServices).containsExactlyInAnyOrderElementsOf(availableServices);
    }

    private List<String> getAvailableServices(Class<?> serviceClass) {
        return reflections
                .getSubTypesOf(serviceClass)
                .stream()
                .filter(this::isConcreteClass)
                .map(Class::getName)
                .collect(Collectors.toList());
    }

    private boolean isConcreteClass(Class<?> clazz) {
        return !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers());
    }

    private List<String> getRegisteredServices(Class<?> serviceClass) throws IOException {
        String resourcePath = "META-INF/services/" + serviceClass.getName();

        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("META-INF/services/" + serviceClass.getName())) {
            assertThat(inputStream).as(resourcePath).isNotNull();

            return IOUtils.readLines(inputStream, StandardCharsets.UTF_8);
        }
    }

}
