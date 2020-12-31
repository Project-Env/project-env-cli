package ch.repolevedavaj.projectenv.core.installer;

import ch.repolevedavaj.projectenv.core.ProjectToolDetails;

import java.io.File;

public interface ProjectToolInstaller<ToolInstallationConfiguration> {

    ProjectToolDetails installTool(ToolInstallationConfiguration configuration, File toolsDirectory) throws Exception;

}
