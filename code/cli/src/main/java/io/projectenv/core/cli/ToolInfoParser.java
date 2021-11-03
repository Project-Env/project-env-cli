package io.projectenv.core.cli;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.projectenv.core.toolsupport.spi.ToolInfo;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public final class ToolInfoParser {

    private static final Type TOOL_INFOS_TYPE = new TypeToken<Map<String, List<ToolInfo>>>() {
    }.getType();

    private ToolInfoParser() {
        // noop
    }

    public static Map<String, List<ToolInfo>> fromJson(String rawToolInfos) {
        return createGson().fromJson(rawToolInfos, TOOL_INFOS_TYPE);
    }

    public static String toJson(Map<String, List<ToolInfo>> toolInfos) {
        return createGson().toJson(toolInfos, TOOL_INFOS_TYPE);
    }

    private static Gson createGson() {
        return new GsonBuilder().registerTypeAdapter(File.class, new FileTypeAdapter()).create();
    }

    private static class FileTypeAdapter extends TypeAdapter<File> {

        @Override
        public void write(JsonWriter out, File value) throws IOException {
            try {
                out.value(value.getCanonicalPath());
            } catch (IOException e) {
                throw new IllegalStateException("cannot get canonical path from file");
            }
        }

        @Override
        public File read(JsonReader in) throws IOException {
            return new File(in.nextString());
        }

    }

}
