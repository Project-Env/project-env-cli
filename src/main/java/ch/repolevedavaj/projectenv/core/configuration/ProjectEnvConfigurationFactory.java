package ch.repolevedavaj.projectenv.core.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import java.io.File;
import java.net.URL;

public final class ProjectEnvConfigurationFactory {

    private ProjectEnvConfigurationFactory() {

    }

    public static ProjectEnvConfiguration createFromFile(File projectEnvConfigurationFile) throws Exception {
        return createFromUrl(projectEnvConfigurationFile.toURI().toURL());
    }

    public static ProjectEnvConfiguration createFromUrl(URL projectEnvConfigurationFile) throws Exception {
        return new ObjectMapper(new YAMLFactory())
                .registerModule(new Jdk8Module())
                .readValue(projectEnvConfigurationFile, ProjectEnvConfiguration.class);
    }

}
