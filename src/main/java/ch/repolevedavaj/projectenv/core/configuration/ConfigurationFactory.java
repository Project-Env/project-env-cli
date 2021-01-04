package ch.repolevedavaj.projectenv.core.configuration;

import jakarta.xml.bind.JAXBContext;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

public final class ConfigurationFactory {

    private ConfigurationFactory() {

    }

    public static ProjectEnv createFromFile(File projectEnvConfigurationFile) throws Exception {
        return createFromUrl(projectEnvConfigurationFile.toURI().toURL());
    }

    public static ProjectEnv createFromUrl(URL projectEnvConfigurationFile) throws Exception {
        ClassLoader current = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(ConfigurationFactory.class.getClassLoader());

            JAXBContext context = JAXBContext.newInstance(ProjectEnv.class);

            try (InputStream inputStream = projectEnvConfigurationFile.openStream()) {
                return context
                        .createUnmarshaller()
                        .unmarshal(new StreamSource(inputStream), ProjectEnv.class)
                        .getValue();
            }

        } finally {
            Thread.currentThread().setContextClassLoader(current);
        }

    }

}
