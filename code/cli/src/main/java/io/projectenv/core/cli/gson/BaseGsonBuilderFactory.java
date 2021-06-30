package io.projectenv.core.cli.gson;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;

import java.util.ServiceLoader;

public final class BaseGsonBuilderFactory {

    private BaseGsonBuilderFactory() {
        // noop
    }

    public static GsonBuilder createBaseGsonBuilder() {
        var gsonBuilder = new GsonBuilder();
        for (var factory : ServiceLoader.load(TypeAdapterFactory.class, TypeAdapterFactory.class.getClassLoader())) {
            gsonBuilder.registerTypeAdapterFactory(factory);
        }

        return gsonBuilder;
    }

}
