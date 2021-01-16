package ch.projectenv.core.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookupFactory;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;

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
        return new StringSubstitutor(StringLookupFactory.INSTANCE.environmentVariableStringLookup())
                .setEnableUndefinedVariableException(true);
    }

}
