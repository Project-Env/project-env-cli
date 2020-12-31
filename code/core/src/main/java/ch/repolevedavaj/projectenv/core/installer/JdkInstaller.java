package ch.repolevedavaj.projectenv.core.installer;

import ch.repolevedavaj.projectenv.core.ProjectToolType;
import ch.repolevedavaj.projectenv.core.configuration.JdkInstallationConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

public class JdkInstaller extends AbstractProjectToolInstaller<JdkInstallationConfiguration> {

    @Override
    protected File getRelevantProjectToolRoot(File toolInstallationDirectory) {
        File relevantProjectToolRoot = super.getRelevantProjectToolRoot(toolInstallationDirectory);
        if (SystemUtils.IS_OS_MAC) {
            return new File(relevantProjectToolRoot, "Contents/Home");
        }

        return relevantProjectToolRoot;
    }

    @Override
    protected ProjectToolType getProjectToolType() {
        return ProjectToolType.JDK;
    }

    @Override
    protected Map<String, String> getAdditionalExports() {
        return Map.of("JAVA_HOME", StringUtils.EMPTY);
    }

    @Override
    protected List<String> getAdditionalPathElements() {
        return List.of("/bin");
    }

}
