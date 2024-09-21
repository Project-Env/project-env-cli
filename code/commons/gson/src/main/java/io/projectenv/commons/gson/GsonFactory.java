package io.projectenv.commons.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.IOException;
import java.util.ServiceLoader;

public final class GsonFactory {

    private GsonFactory() {
        // noop
    }

    public static Gson createGson() {
        return createGsonBuilder().create();
    }

    public static GsonBuilder createGsonBuilder() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        for (TypeAdapterFactory factory : ServiceLoader.load(TypeAdapterFactory.class, TypeAdapterFactory.class.getClassLoader())) {
            gsonBuilder.registerTypeAdapterFactory(factory);
        }
        gsonBuilder.registerTypeAdapter(File.class, new FileTypeAdapter());

        return gsonBuilder;
    }

    private static class FileTypeAdapter extends TypeAdapter<File> {

        @Override
        public void write(JsonWriter out, File value) throws IOException {
            if (value != null) {
                out.value(value.getCanonicalPath());
            } else {
                out.nullValue();
            }
        }

        @Override
        public File read(JsonReader in) throws IOException {
            return new File(in.nextString());
        }

    }

}
