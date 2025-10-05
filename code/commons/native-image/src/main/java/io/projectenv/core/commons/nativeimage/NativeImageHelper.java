package io.projectenv.core.commons.nativeimage;

import io.projectenv.core.commons.process.ProcessOutput;
import org.graalvm.nativeimage.hosted.RuntimeClassInitialization;
import org.graalvm.nativeimage.hosted.RuntimeReflection;
import org.graalvm.nativeimage.hosted.RuntimeResourceAccess;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.reflections.scanners.Scanners.SubTypes;

public final class NativeImageHelper {

    private static final String SERVICE_REGISTRATION_FILE_PATH = "META-INF/services/";

    private NativeImageHelper() {
        // noop
    }

    public static void registerResource(String resource) {
        ProcessOutput.writeInfoMessage("Registering resource {0} in native image", resource);
        RuntimeResourceAccess.addResource(NativeImageHelper.class.getModule(), resource);
    }

    public static void initializeAtBuildTime(Class<?> clazz) {
        RuntimeClassInitialization.initializeAtBuildTime(clazz);
    }

    public static void registerService(Class<?> clazz) {
        registerClassAndSubclassesForReflection(clazz);
        registerResource(SERVICE_REGISTRATION_FILE_PATH + clazz.getName());
    }

    public static void registerClassAndSubclassesForReflection(Class<?> clazz) {
        registerClassForReflection(clazz);
        Reflections reflections = new Reflections(new ConfigurationBuilder().forPackages("io.projectenv").setScanners(SubTypes));
        for (Class<?> subClazz : reflections.get(SubTypes.of(clazz).asClass())) {
            registerClassForReflection(subClazz);
        }
    }

    public static void registerFieldsWithAnnotationForReflection(String basePackage, Class<? extends Annotation> annotationClazz) {
        ProcessOutput.writeInfoMessage("Scanning package {0} for fields with annotation {1}", basePackage, annotationClazz.getSimpleName());

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages(basePackage)
                .setScanners(Scanners.FieldsAnnotated));

        for (Field field : reflections.getFieldsAnnotatedWith(annotationClazz)) {
            ProcessOutput.writeInfoMessage("Registering field {0} for reflection in native image", field.getName());
            RuntimeReflection.register(field);
        }
    }

    public static void registerClassForReflection(Class<?> clazz) {
        ProcessOutput.writeInfoMessage("Registering class {0} for reflection in native image", clazz.getName());
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

    private static boolean isConcreteClass(Class<?> clazz) {
        return !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers());
    }

}
