package io.projectenv.cli.gson;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.projectenv.cli.common.ServiceLoaderHelper;

import java.io.File;
import java.io.IOException;

public final class BaseGsonBuilderFactory {

    private BaseGsonBuilderFactory() {
        // noop
    }

    public static GsonBuilder createBaseGsonBuilder() {
        var gsonBuilder = new GsonBuilder();
        for (var factory : ServiceLoaderHelper.loadService(TypeAdapterFactory.class)) {
            gsonBuilder.registerTypeAdapterFactory(factory);
        }

        gsonBuilder.registerTypeAdapter(File.class, new FileTypeAdapter());

        return gsonBuilder;
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
