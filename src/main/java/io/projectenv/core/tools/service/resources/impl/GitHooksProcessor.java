package io.projectenv.core.tools.service.resources.impl;

import io.projectenv.core.tools.info.GitHooksInfo;
import io.projectenv.core.tools.info.ToolInfo;
import io.projectenv.core.tools.service.ToolSpecificServiceContext;
import io.projectenv.core.tools.service.resources.LocalToolResourcesProcessor;
import io.projectenv.core.tools.service.resources.LocalToolResourcesProcessorException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class GitHooksProcessor implements LocalToolResourcesProcessor<GitHooksInfo> {

    private static final String RELATIVE_GIT_HOOKS_PATH = ".git/hooks";

    @Override
    public void processLocalToolResources(GitHooksInfo toolInfo, ToolSpecificServiceContext context) throws LocalToolResourcesProcessorException {
        try {
            File source = toolInfo.getDirectory();
            File target = new File(context.getProjectRoot(), RELATIVE_GIT_HOOKS_PATH);

            if (source.exists() && source.isDirectory()) {
                FileUtils.copyDirectory(source, target);
            }
        } catch (IOException e) {
            throw new LocalToolResourcesProcessorException("failed to copy Git hooks", e);
        }
    }

    @Override
    public boolean supportsTool(ToolInfo toolInfo) {
        return toolInfo instanceof GitHooksInfo;
    }

}
