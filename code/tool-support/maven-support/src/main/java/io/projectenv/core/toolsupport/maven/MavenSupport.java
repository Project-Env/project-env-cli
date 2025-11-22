package io.projectenv.core.toolsupport.maven;

import io.projectenv.core.toolsupport.commons.AbstractUpgradableToolSupport;
import io.projectenv.core.toolsupport.commons.commands.*;
import io.projectenv.core.toolsupport.spi.ImmutableToolInfo;
import io.projectenv.core.toolsupport.spi.ToolInfo;
import io.projectenv.core.toolsupport.spi.ToolSupportContext;
import io.projectenv.core.toolsupport.spi.ToolSupportException;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationDetails;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationManagerException;
import io.projectenv.core.toolsupport.spi.installation.LocalToolInstallationStep;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MavenSupport extends AbstractUpgradableToolSupport<MavenConfiguration> {

    // Maven-specific constants
    private static final String MAVEN_CONF_SETTINGS_PATH = "conf/settings.xml";
    private static final String DEFAULT_USER_MAVEN_DIR = ".m2";
    private static final String SETTINGS_XML = "settings.xml";

    // Metadata keys
    private static final String METADATA_VERSION = "version";
    private static final String METADATA_GLOBAL_SETTINGS_FILE = "globalSettingsFile";
    private static final String METADATA_USER_SETTINGS_FILE = "userSettingsFile";

    @Override
    public String getToolIdentifier() {
        return "maven";
    }

    @Override
    public String getName(MavenConfiguration toolConfiguration) {
        return "Maven";
    }

    @Override
    public Class<MavenConfiguration> getToolConfigurationClass() {
        return MavenConfiguration.class;
    }

    @Override
    public ToolInfo prepareTool(MavenConfiguration toolConfiguration, ToolSupportContext context) throws ToolSupportException {
        var toolInstallationDetails = installTool(toolConfiguration, context);

        return createProjectEnvToolInfo(toolConfiguration, toolInstallationDetails, context);
    }

    @Override
    public String getDescription(MavenConfiguration toolConfiguration) {
        StringBuilder description = new StringBuilder(super.getDescription(toolConfiguration));
        if (toolConfiguration.getGlobalSettingsFile().isPresent() || toolConfiguration.getUserSettingsFile().isPresent()) {
            description.append(" with ");
            if (toolConfiguration.getGlobalSettingsFile().isPresent()) {
                description.append("global ");
                if (toolConfiguration.getUserSettingsFile().isPresent()) {
                    description.append("and user ");
                }
            }
            else if (toolConfiguration.getUserSettingsFile().isPresent()) {
                description.append("user ");
            }
            description.append("settings");
        }
        return description.toString();
    }

    @Override
    protected String getCurrentVersion(MavenConfiguration toolConfiguration) {
        return toolConfiguration.getVersion();
    }

    @Override
    protected Set<String> getAllValidVersions(MavenConfiguration toolConfiguration, ToolSupportContext context) {
        return context.getToolsIndexManager().getMavenVersions();
    }

    private LocalToolInstallationDetails installTool(MavenConfiguration toolConfiguration, ToolSupportContext context) throws ToolSupportException {
        try {
            var steps = createInstallationSteps(toolConfiguration, context);

            return context.getLocalToolInstallationManager().installOrUpdateTool(getToolIdentifier(), steps);
        } catch (LocalToolInstallationManagerException e) {
            throw new ToolSupportException("Failed to install tool", e);
        }
    }

    private List<LocalToolInstallationStep> createInstallationSteps(MavenConfiguration toolConfiguration, ToolSupportContext context) {
        List<LocalToolInstallationStep> steps = new ArrayList<>();

        steps.add(new ExtractArchiveStep(getSystemSpecificDownloadUri(toolConfiguration, context), context.getHttpClientProvider()));
        steps.add(new FindBinariesRootStep());
        steps.add(new RegisterPathElementStep("/bin"));
        steps.add(new RegisterMainExecutableStep("mvn"));
        steps.add(new RegisterEnvironmentVariableStep("MAVEN_HOME", "/"));

        toolConfiguration
                .getGlobalSettingsFile()
                .ifPresent(s -> steps.add(new OverwriteFileStep(context.getProjectRoot(), s, MAVEN_CONF_SETTINGS_PATH)));

        for (var rawPostExtractionCommand : toolConfiguration.getPostExtractionCommands()) {
            steps.add(new ExecuteCommandStep(rawPostExtractionCommand, context.getProjectRoot()));
        }

        return steps;
    }

    private String getSystemSpecificDownloadUri(MavenConfiguration toolConfiguration, ToolSupportContext context) {
        return context.getToolsIndexManager().resolveMavenDistributionUrl(toolConfiguration.getVersion());
    }

    /**
     * Creates ToolInfo with Maven-specific metadata.
     * <p>
     * Metadata includes:
     * <ul>
     *   <li><b>version</b>: Maven version from configuration</li>
     *   <li><b>globalSettingsFile</b>: Path to ${maven.home}/conf/settings.xml (always present)</li>
     *   <li><b>userSettingsFile</b>: Path to user settings if configured or found at default location</li>
     * </ul>
     * <p>
     * User settings resolution priority:
     * <ol>
     *   <li>Project-specific file configured in project-env.toml (if exists)</li>
     *   <li>Default location ${user.home}/.m2/settings.xml (if exists and not configured)</li>
     * </ol>
     *
     * @param toolConfiguration the Maven configuration
     * @param localToolInstallationDetails the installation details
     * @param context the tool support context
     * @return ToolInfo with populated metadata
     */
    private ToolInfo createProjectEnvToolInfo(MavenConfiguration toolConfiguration,
                                              LocalToolInstallationDetails localToolInstallationDetails,
                                              ToolSupportContext context) {

        var builder = ImmutableToolInfo.builder()
                .toolBinariesRoot(localToolInstallationDetails.getBinariesRoot())
                .primaryExecutable(localToolInstallationDetails.getPrimaryExecutable())
                .environmentVariables(localToolInstallationDetails.getEnvironmentVariables())
                .pathElements(localToolInstallationDetails.getPathElements())
                .handledProjectResources(localToolInstallationDetails.getFileOverwrites()
                        .stream().map(Pair::getLeft)
                        .toList());

        addMavenMetadata(builder, toolConfiguration, localToolInstallationDetails, context);

        return builder.build();
    }

    /**
     * Adds Maven-specific metadata to the ToolInfo builder.
     */
    private void addMavenMetadata(ImmutableToolInfo.Builder builder,
                                  MavenConfiguration toolConfiguration,
                                  LocalToolInstallationDetails localToolInstallationDetails,
                                  ToolSupportContext context) {

        // Add version metadata
        builder.putToolSpecificMetadata(METADATA_VERSION, toolConfiguration.getVersion());

        // Add global settings file (always present in Maven installation)
        addGlobalSettingsMetadata(builder, localToolInstallationDetails);

        // Add user settings file (if configured or exists at default location)
        addUserSettingsMetadata(builder, toolConfiguration, context);
    }

    /**
     * Adds global settings.xml path to metadata.
     * Maven's global settings are always located at ${maven.home}/conf/settings.xml
     */
    private void addGlobalSettingsMetadata(ImmutableToolInfo.Builder builder,
                                           LocalToolInstallationDetails localToolInstallationDetails) {
        localToolInstallationDetails.getBinariesRoot().ifPresent(binariesRoot -> {
            var globalSettingsFile = new File(binariesRoot, MAVEN_CONF_SETTINGS_PATH);
            builder.putToolSpecificMetadata(METADATA_GLOBAL_SETTINGS_FILE, globalSettingsFile.getAbsolutePath());
        });
    }

    /**
     * Adds user settings.xml path to metadata if found.
     * <p>
     * Resolution priority:
     * <ol>
     *   <li>Project-specific file from project-env.toml configuration</li>
     *   <li>Default user settings at ${user.home}/.m2/settings.xml</li>
     * </ol>
     */
    private void addUserSettingsMetadata(ImmutableToolInfo.Builder builder,
                                         MavenConfiguration toolConfiguration,
                                         ToolSupportContext context) {

        if (toolConfiguration.getUserSettingsFile().isPresent()) {
            // Use configured user settings file
            addConfiguredUserSettings(builder, toolConfiguration, context);
        } else {
            // Fall back to default user settings location
            addDefaultUserSettings(builder);
        }
    }

    /**
     * Adds user settings file configured in project-env.toml (if it exists).
     */
    private void addConfiguredUserSettings(ImmutableToolInfo.Builder builder,
                                           MavenConfiguration toolConfiguration,
                                           ToolSupportContext context) {
        toolConfiguration.getUserSettingsFile().ifPresent(configuredPath -> {
            var userSettingsFile = new File(context.getProjectRoot(), configuredPath);
            if (userSettingsFile.exists()) {
                builder.putToolSpecificMetadata(METADATA_USER_SETTINGS_FILE, userSettingsFile.getAbsolutePath());
                builder.putUnhandledProjectResources(METADATA_USER_SETTINGS_FILE, userSettingsFile);
            }
        });
    }

    /**
     * Adds default user settings file from ${user.home}/.m2/settings.xml (if it exists).
     */
    private void addDefaultUserSettings(ImmutableToolInfo.Builder builder) {
        String userHome = System.getProperty("user.home");
        if (StringUtils.isNotBlank(userHome)) {
            var defaultUserSettingsFile = new File(new File(userHome, DEFAULT_USER_MAVEN_DIR), SETTINGS_XML);
            if (defaultUserSettingsFile.exists()) {
                builder.putToolSpecificMetadata(METADATA_USER_SETTINGS_FILE, defaultUserSettingsFile.getAbsolutePath());
            }
        }
    }

}
