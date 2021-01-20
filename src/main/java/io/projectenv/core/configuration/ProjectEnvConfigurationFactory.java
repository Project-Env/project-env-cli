package io.projectenv.core.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookup;
import org.apache.commons.text.lookup.StringLookupFactory;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class ProjectEnvConfigurationFactory {

    private ProjectEnvConfigurationFactory() {

    }

    public static ProjectEnvConfiguration createFromFile(File projectEnvConfigurationFile) throws Exception {
        return createFromUrl(projectEnvConfigurationFile.toURI().toURL());
    }

    public static ProjectEnvConfiguration createFromUrl(URL projectEnvConfigurationFile) throws Exception {
        String rawConfiguration = IOUtils.toString(projectEnvConfigurationFile, StandardCharsets.UTF_8);
        String interpolatedString = createStringSubstitutor().replace(rawConfiguration);

        return new ObjectMapper(new YAMLFactory())
                .registerModule(new Jdk8Module())
                .readValue(interpolatedString, ProjectEnvConfiguration.class);
    }

    private static StringSubstitutor createStringSubstitutor() {
        return new StringSubstitutor(new FallbackEnvironmentVariableStringLookup())
                .setEnableUndefinedVariableException(true);
    }

    private static class FallbackEnvironmentVariableStringLookup implements StringLookup {

        private final BidiMap<String, String> FALLBACK_MAPPING = new DualHashBidiMap<>(Map.of(
                "USER", "USERNAME"
        ));

        @Override
        public String lookup(String key) {
            String value = lookupParent(key);
            if (value != null) {
                return value;
            }

            if (FALLBACK_MAPPING.containsKey(key)) {
                return lookupParent(FALLBACK_MAPPING.get(key));
            }

            if (FALLBACK_MAPPING.inverseBidiMap().containsKey(key)) {
                return lookupParent(FALLBACK_MAPPING.inverseBidiMap().get(key));
            }

            return null;
        }

        private String lookupParent(String key) {
            return StringLookupFactory.INSTANCE.environmentVariableStringLookup().lookup(key);
        }

    }

}
