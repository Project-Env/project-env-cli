package io.projectenv.core.tools.repository;

import io.projectenv.core.configuration.ToolConfiguration;
import io.projectenv.core.tools.info.ToolInfo;

import java.io.File;
import java.util.List;

public interface ToolsRepository {

    List<ToolInfo> requestTools(List<ToolConfiguration> requestedTools, File projectRoot) throws ToolsRepositoryException;

    void cleanAllToolsOfCurrentOSExcluding(List<ToolConfiguration> excludedTools) throws ToolsRepositoryException;

}
