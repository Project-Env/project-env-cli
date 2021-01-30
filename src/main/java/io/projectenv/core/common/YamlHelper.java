package io.projectenv.core.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import java.io.File;
import java.io.IOException;

public final class YamlHelper {

    private YamlHelper() {
        // noop
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(new YAMLFactory()).registerModule(new Jdk8Module());

    public static <T> T readValue(String source, Class<T> clazz) throws IOException {
        return OBJECT_MAPPER.readValue(source, clazz);
    }

    public static <T> void writeValue(T value, File target) throws IOException {
        OBJECT_MAPPER.writeValue(target, value);
    }

    public static <T> String writeValueAsString(T value) throws IOException {
        return OBJECT_MAPPER.writeValueAsString(value);
    }

}
