package io.projectenv.core.cli;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Provides version information from the build-time generated version.properties file.
 */
public final class VersionProvider {

    private static final String VERSION_PROPERTIES = "/version.properties";
    private static final String VERSION_KEY = "version";
    private static final String UNKNOWN_VERSION = "unknown";

    private static final String VERSION = loadVersion();

    private VersionProvider() {
        // Utility class
    }

    /**
     * Returns the project version as configured during Maven build.
     *
     * @return the project version, or "unknown" if it cannot be determined
     */
    public static String getVersion() {
        return VERSION;
    }

    private static String loadVersion() {
        try (InputStream input = VersionProvider.class.getResourceAsStream(VERSION_PROPERTIES)) {
            if (input == null) {
                System.err.println("Warning: version.properties not found in classpath");
                return UNKNOWN_VERSION;
            }

            Properties properties = new Properties();
            properties.load(input);
            return properties.getProperty(VERSION_KEY, UNKNOWN_VERSION);
        } catch (IOException e) {
            System.err.println("Warning: Failed to load version.properties: " + e.getMessage());
            return UNKNOWN_VERSION;
        }
    }

}

