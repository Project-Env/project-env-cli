package io.projectenv.core.commons.nativeimage;

import com.oracle.svm.core.jdk.Resources;
import com.oracle.svm.core.jdk.proxy.DynamicProxyRegistry;
import io.projectenv.core.commons.process.ProcessOutput;
import org.graalvm.nativeimage.ImageSingletons;
import org.graalvm.nativeimage.hosted.RuntimeReflection;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Collectors;

public final class NativeImageHelper {

    private static final String SERVICE_REGISTRATION_FILE_PATH = "META-INF/services/";

    private NativeImageHelper() {
        // noop
    }

    public static void registerResource(String resource) throws IOException {
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(resource)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("resource " + resource + " could not be resolved");
            }

            ProcessOutput.writeInfoMessage("registering resource {0} in native image", resource);
            Resources.registerResource(resource, inputStream);
        }
    }

    public static void registerService(Class<?> clazz) throws IOException {
        registerClassAndSubclassesForReflection(clazz);
        registerResource(SERVICE_REGISTRATION_FILE_PATH + clazz.getName());
    }

    public static void registerClassAndSubclassesForReflection(Class<?> clazz) {
        Reflections reflections = new Reflections();
        for (Class<?> subClazz : reflections.getSubTypesOf(clazz)) {
            registerClassForReflection(subClazz);
        }
    }

    public static void registerFieldsWithAnnotationForReflection(String basePackage, Class<? extends Annotation> annotationClazz) {
        ProcessOutput.writeInfoMessage("scanning package {0} for fields with annotation {1}", basePackage, annotationClazz.getSimpleName());

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages(basePackage)
                .setScanners(Scanners.FieldsAnnotated));

        for (Field field : reflections.getFieldsAnnotatedWith(annotationClazz)) {
            ProcessOutput.writeInfoMessage("registering field {0} for reflection in native image", field.getName());
            RuntimeReflection.register(field);
        }
    }

    public static void registerClassForReflection(Class<?> clazz) {
        ProcessOutput.writeInfoMessage("registering class {0} for reflection in native image", clazz.getName());
        RuntimeReflection.register(clazz);
        RuntimeReflection.register(clazz.getDeclaredMethods());
        RuntimeReflection.register(clazz.getDeclaredFields());

        if (isConcreteClass(clazz)) {
            RuntimeReflection.register(clazz.getDeclaredConstructors());
        }

        for (Class<?> innerClazz : clazz.getDeclaredClasses()) {
            registerClassForReflection(innerClazz);
        }
    }

    public static void registerDynamicProxy(Class<?>... interfaces) {
        ProcessOutput.writeInfoMessage("registering dynamic proxy for interfaces {0} in native image", asJoinedStringList(interfaces));

        ImageSingletons.lookup(DynamicProxyRegistry.class).addProxyClass(interfaces);
    }

    private static String asJoinedStringList(Class<?>... interfaces) {
        return Arrays.stream(interfaces).map(Class::getName).collect(Collectors.joining(", "));
    }

    private static boolean isConcreteClass(Class<?> clazz) {
        return !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers());
    }

}
