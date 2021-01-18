package ch.projectenv.core.installer;

import ch.projectenv.core.configuration.MavenConfiguration;
import ch.projectenv.core.toolinfo.MavenInfo;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.Files;

public class MavenInstaller extends AbstractProjectToolInstaller<MavenConfiguration, MavenInfo> {

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

        Files.createSymbolicLink(link.toPath(), link.getParentFile().toPath().relativize(target.toPath()));
    }

}
