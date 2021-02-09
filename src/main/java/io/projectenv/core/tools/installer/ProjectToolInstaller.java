package io.projectenv.core.tools.installer;

import io.projectenv.core.configuration.ToolConfiguration;

public interface ProjectToolInstaller<T extends ToolConfiguration> {

    void installTool(T configuration, ProjectToolInstallerContext context) throws ProjectToolInstallerException;

    boolean supportsTool(ToolConfiguration toolConfiguration);

}
