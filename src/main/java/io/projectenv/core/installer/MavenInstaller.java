package io.projectenv.core.installer;

import io.projectenv.core.configuration.MavenConfiguration;
import io.projectenv.core.toolinfo.MavenInfo;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;

public class MavenInstaller extends AbstractProjectToolInstaller<MavenConfiguration, MavenInfo> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String RELATIVE_GLOBAL_SETTINGS_FILE_PATH = "conf/settings.xml";

    @Override
    protected Class<MavenConfiguration> getToolConfigurationClass() {
        return MavenConfiguration.class;
    }

    @Override
    protected void executePostInstallationSteps(MavenConfiguration configuration, MavenInfo toolInfo, ProjectToolInstallerContext context) throws Exception {
        linkGlobalSettingsFile(toolInfo);
    }

    private void linkGlobalSettingsFile(MavenInfo toolInfo) throws Exception {
        if (toolInfo.getGlobalSettingsFile().isEmpty()) {
            return;
        }

        File target = toolInfo.getGlobalSettingsFile().get();
        if (!target.exists()) {
            return;
        }

        File link = new File(toolInfo.getLocation(), RELATIVE_GLOBAL_SETTINGS_FILE_PATH);
        FileUtils.forceMkdirParent(link);
        if (link.exists()) {
            FileUtils.forceDelete(link);
        }

        try {
            Files.createSymbolicLink(link.toPath(), link.getParentFile().toPath().relativize(target.toPath()));
        } catch (Exception e) {
            log.warn("failed to link global Maven settings file - copy file as a fallback (will not be updated automatically)", e);
            FileUtils.copyFile(target, link);
        }
    }

}
