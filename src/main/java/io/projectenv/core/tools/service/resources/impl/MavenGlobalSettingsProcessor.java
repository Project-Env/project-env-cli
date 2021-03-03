package io.projectenv.core.tools.service.resources.impl;

import io.projectenv.core.tools.info.MavenInfo;
import io.projectenv.core.tools.info.ToolInfo;
import io.projectenv.core.tools.service.ToolSpecificServiceContext;
import io.projectenv.core.tools.service.resources.LocalToolResourcesProcessor;
import io.projectenv.core.tools.service.resources.LocalToolResourcesProcessorException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class MavenGlobalSettingsProcessor implements LocalToolResourcesProcessor<MavenInfo> {

    private static final String RELATIVE_GLOBAL_SETTINGS_FILE_PATH = "conf/settings.xml";

    @Override
    public void processLocalToolResources(MavenInfo toolInfo, ToolSpecificServiceContext context) throws LocalToolResourcesProcessorException {
        try {
            copyGlobalSettingsFile(toolInfo);
        } catch (IOException e) {
            throw new LocalToolResourcesProcessorException("failed to update Maven settings file", e);
        }
    }

    @Override
    public boolean supportsTool(ToolInfo toolInfo) {
        return toolInfo instanceof MavenInfo;
    }

    private void copyGlobalSettingsFile(MavenInfo toolInfo) throws IOException {
        if (toolInfo.getGlobalSettingsFile().isEmpty()) {
            return;
        }

        File source = toolInfo.getGlobalSettingsFile().get();
        if (!source.exists()) {
            return;
        }

        File target = new File(toolInfo.getLocation(), RELATIVE_GLOBAL_SETTINGS_FILE_PATH);
        FileUtils.forceMkdirParent(target);
        if (target.exists()) {
            FileUtils.forceDelete(target);
        }

        FileUtils.copyFile(source, target);
    }

}
