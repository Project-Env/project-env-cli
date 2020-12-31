package ch.repolevedavaj.projectenv.core.configuration;

import jakarta.xml.bind.JAXBContext;

import javax.xml.transform.stream.StreamSource;
import java.io.File;

public final class ConfigurationFactory {

    private ConfigurationFactory() {

    }

    public static ProjectEnv createFromFile(File projectEnvConfigurationFile) throws Exception {
        ClassLoader current = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(ConfigurationFactory.class.getClassLoader());

            JAXBContext context = JAXBContext.newInstance(ProjectEnv.class);
            return context
                    .createUnmarshaller()
                    .unmarshal(new StreamSource(projectEnvConfigurationFile), ProjectEnv.class)
                    .getValue();

        } finally {
            Thread.currentThread().setContextClassLoader(current);
        }

    }

}
