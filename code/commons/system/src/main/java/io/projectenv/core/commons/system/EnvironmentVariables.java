package io.projectenv.core.commons.system;

public class EnvironmentVariables {

    private EnvironmentVariables() {
        // noop
    }

    public static String get(String key) {
        return System.getenv(key);
    }

}
