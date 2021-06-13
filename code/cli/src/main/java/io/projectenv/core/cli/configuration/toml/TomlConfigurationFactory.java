package io.projectenv.core.cli.configuration.toml;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.moandjiezana.toml.Toml;
import io.projectenv.core.cli.configuration.ImmutableProjectEnvBaseConfiguration;
import io.projectenv.core.cli.configuration.ProjectEnvBaseConfiguration;
import io.projectenv.core.cli.configuration.ProjectEnvConfiguration;
import io.projectenv.core.cli.gson.BaseGsonBuilderFactory;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookup;
import org.apache.commons.text.lookup.StringLookupFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class TomlConfigurationFactory {

    private static final Gson GSON = BaseGsonBuilderFactory
            .createBaseGsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    private TomlConfigurationFactory() {
        // noop
    }

    public static ProjectEnvConfiguration fromFile(File projectEnvConfigurationFile) throws IOException {
        return fromUrl(projectEnvConfigurationFile.toURI().toURL());
    }

    public static ProjectEnvConfiguration fromUrl(URL projectEnvConfigurationFile) throws IOException {
        var rawConfiguration = IOUtils.toString(projectEnvConfigurationFile, StandardCharsets.UTF_8);
        var interpolatedString = createStringSubstitutor().replace(rawConfiguration);

        var toml = new Toml().read(interpolatedString);
        return new TomlProjectEnvConfiguration(toml);
    }

    private static StringSubstitutor createStringSubstitutor() {
        return new StringSubstitutor(new FallbackEnvironmentVariableStringLookup())
                .setEnableUndefinedVariableException(true);
    }

    private static class FallbackEnvironmentVariableStringLookup implements StringLookup {

        private static final BidiMap<String, String> FALLBACK_MAPPING = new DualHashBidiMap<>(Map.of(
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

    private static class TomlProjectEnvConfiguration implements ProjectEnvConfiguration {

        private final Toml toml;
        private final ProjectEnvBaseConfiguration projectEnvBaseConfiguration;

        TomlProjectEnvConfiguration(Toml toml) {
            this.toml = toml;

            this.projectEnvBaseConfiguration = readAs(toml, ImmutableProjectEnvBaseConfiguration.class);
        }

        @Override
        public String getToolsDirectory() {
            return projectEnvBaseConfiguration.getToolsDirectory();
        }

        @Override
        public <T> List<T> getToolConfigurations(String toolName, Class<T> configurationClass) {
            if (toml.containsTableArray(toolName)) {
                return readAs(toml.getTables(toolName), configurationClass);
            }

            if (toml.containsTable(toolName)) {
                return readAs(List.of(toml.getTable(toolName)), configurationClass);
            }

            return Collections.emptyList();
        }

        private static <T> List<T> readAs(List<Toml> tomlList, Class<T> configurationClass) {
            return List.copyOf(tomlList.stream()
                    .map(table -> readAs(table, configurationClass))
                    .collect(Collectors.toList()));
        }

        private static <T> T readAs(Toml toml, Class<T> configurationClass) {
            return GSON.fromJson(GSON.toJson(toml.toMap()), configurationClass);
        }

    }


}
