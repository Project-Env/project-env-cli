package io.projectenv.core.cli.configuration;

import java.util.List;

public interface ProjectEnvConfiguration extends ProjectEnvBaseConfiguration {

    <T> List<T> getToolConfigurations(String toolName, Class<T> configurationClass);

}
