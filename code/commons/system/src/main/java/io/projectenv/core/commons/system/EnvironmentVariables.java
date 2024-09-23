package io.projectenv.core.commons.system;

import java.util.Map;

public class EnvironmentVariables {

    private EnvironmentVariables() {
        // noop
    }

    public static String get(String key) {
        return get().get(key);
    }

    public static Map<String, String> get() {
        return System.getenv();
    }

}
