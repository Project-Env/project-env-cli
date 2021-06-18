package io.projectenv.cli.configuration;

import io.projectenv.cli.configuration.ProjectEnvBaseConfiguration;

import java.util.List;

public interface ProjectEnvConfiguration extends ProjectEnvBaseConfiguration {

    <T> List<T> getToolConfigurations(String toolName, Class<T> configurationClass);

}
