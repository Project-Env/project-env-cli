package io.projectenv.core.commons.nativeimage;

import com.oracle.svm.core.jdk.Resources;
import io.projectenv.core.commons.process.ProcessOutputWriter;
import io.projectenv.core.commons.process.ProcessOutputWriterAccessor;
import org.graalvm.nativeimage.hosted.RuntimeReflection;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;

public final class NativeImageHelper {

    private static final String SERVICE_REGISTRATION_FILE_PATH = "META-INF/services/";

    private static final ProcessOutputWriter PROCESS_INFO_WRITER = ProcessOutputWriterAccessor.getProcessInfoWriter();

    private NativeImageHelper() {
        // noop
    }

    public static void registerResource(String resource) throws IOException {
        try (var inputStream = ClassLoader.getSystemResourceAsStream(resource)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("resource " + resource + " could not be resolved");
            }

            PROCESS_INFO_WRITER.write("registering resource {0} in native image", resource);
            Resources.registerResource(resource, inputStream);
        }
    }

    public static void registerService(Class<?> clazz) throws IOException {
        registerClassAndSubclassesForReflection(clazz);
        registerResource(SERVICE_REGISTRATION_FILE_PATH + clazz.getName());
    }

    public static void registerClassAndSubclassesForReflection(Class<?> clazz) {
        var reflections = new Reflections();
        for (var subClazz : reflections.getSubTypesOf(clazz)) {
            registerClassForReflection(subClazz);
        }
    }

    public static void registerFieldsWithAnnotationForReflection(String basePackage, Class<? extends Annotation> annotationClazz) {
        PROCESS_INFO_WRITER.write("scanning package {0} for fields with annotation {1}", basePackage, annotationClazz.getSimpleName());

        var reflections = new Reflections(new ConfigurationBuilder()
                .forPackages(basePackage)
                .setScanners(new FieldAnnotationsScanner()));

        for (var field : reflections.getFieldsAnnotatedWith(annotationClazz)) {
            PROCESS_INFO_WRITER.write("registering field {0} for reflection in native image", field.getName());
            RuntimeReflection.register(field);
        }
    }

    public static void registerClassForReflection(Class<?> clazz) {
        PROCESS_INFO_WRITER.write("registering class {0} for reflection in native image", clazz.getName());
        RuntimeReflection.register(clazz);
        RuntimeReflection.register(clazz.getDeclaredMethods());
        RuntimeReflection.register(clazz.getDeclaredFields());

        if (isConcreteClass(clazz)) {
            RuntimeReflection.register(clazz.getDeclaredConstructors());
        }

        for (var innerClazz : clazz.getDeclaredClasses()) {
            registerClassForReflection(innerClazz);
        }
    }

    private static boolean isConcreteClass(Class<?> clazz) {
        return !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers());
    }

}
