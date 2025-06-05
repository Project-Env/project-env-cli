package io.projectenv.core.toolsupport.commons.commands;

import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationDetails;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationStep;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationStepException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookupFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Map;

public class ReplaceInFileStep implements LocalToolInstallationStep {

    private final String rawTarget;
    private final String searchString;
    private final String replacement;

    public ReplaceInFileStep(String rawTarget, String searchString, String replacement) {
        this.rawTarget = rawTarget;
        this.searchString = searchString;
        this.replacement = replacement;
    }

    @Override
    public LocalToolInstallationDetails executeInstallStep(File installationRoot, LocalToolInstallationDetails intermediateInstallationDetails) throws LocalToolInstallationStepException {
        try {
            var target = new File(intermediateInstallationDetails.getBinariesRoot().orElse(installationRoot), rawTarget);
            if (!target.exists()) {
                return intermediateInstallationDetails;
            }

            var interpolatedReplacement = createStringSubstitutor(installationRoot, intermediateInstallationDetails).replace(replacement);

            String content = FileUtils.readFileToString(target, StandardCharsets.UTF_8);
            content = content.replace(searchString, interpolatedReplacement);
            Files.writeString(target.toPath(), content, StandardCharsets.UTF_8);

            return intermediateInstallationDetails;
        } catch (IOException e) {
            throw new LocalToolInstallationStepException("failed to execute step", e);
        }
    }

    @Override
    public LocalToolInstallationDetails executeUpdateStep(File installationRoot, LocalToolInstallationDetails intermediateInstallationDetails) throws LocalToolInstallationStepException {
        return executeInstallStep(installationRoot, intermediateInstallationDetails);
    }

    @Override
    public void updateChecksum(MessageDigest digest) {
        digest.update(rawTarget.getBytes(StandardCharsets.UTF_8));
        digest.update(searchString.getBytes(StandardCharsets.UTF_8));
        digest.update(replacement.getBytes(StandardCharsets.UTF_8));
    }

    private StringSubstitutor createStringSubstitutor(File installationRoot, LocalToolInstallationDetails intermediateInstallationDetails) throws IOException {
        return new StringSubstitutor(StringLookupFactory.INSTANCE.mapStringLookup(Map.of(
                "PROJECT_ENV_TOOL_ROOT", getProjectEnvToolRoot(installationRoot, intermediateInstallationDetails)
        )));
    }

    private String getProjectEnvToolRoot(File installationRoot, LocalToolInstallationDetails intermediateInstallationDetails) throws IOException {
        return intermediateInstallationDetails.getBinariesRoot().orElse(installationRoot).getCanonicalPath();
    }

}
