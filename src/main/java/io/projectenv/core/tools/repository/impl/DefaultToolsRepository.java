package io.projectenv.core.tools.repository.impl;

import io.projectenv.core.common.OperatingSystem;
import io.projectenv.core.common.YamlHelper;
import io.projectenv.core.common.lock.LockFile;
import io.projectenv.core.common.lock.LockFileHelper;
import io.projectenv.core.configuration.ToolConfiguration;
import io.projectenv.core.tools.collector.ImmutableToolInfoCollectorContext;
import io.projectenv.core.tools.collector.ToolInfoCollectorContext;
import io.projectenv.core.tools.collector.ToolInfoCollectors;
import io.projectenv.core.tools.info.ToolInfo;
import io.projectenv.core.tools.installer.ImmutableProjectToolInstallerContext;
import io.projectenv.core.tools.installer.ProjectToolInstallerContext;
import io.projectenv.core.tools.installer.ProjectToolInstallerException;
import io.projectenv.core.tools.installer.ToolInstallers;
import io.projectenv.core.tools.repository.ToolsRepository;
import io.projectenv.core.tools.repository.ToolsRepositoryException;
import io.projectenv.core.tools.repository.impl.catalogue.ImmutableToolCatalogue;
import io.projectenv.core.tools.repository.impl.catalogue.ToolCatalogue;
import io.projectenv.core.tools.repository.impl.catalogue.ToolEntry;
import io.projectenv.core.tools.repository.impl.catalogue.ToolEntryFactory;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class DefaultToolsRepository implements ToolsRepository {

    private static final String LOCK_FILE = ".lock";
    private static final String REPOSITORY_CATALOGUE = "catalogue.yml";

    private static final OperatingSystem CURRENT_OS = OperatingSystem.getCurrentOS();

    private final File repositoryRoot;

    public DefaultToolsRepository(File repositoryRoot) {
        this.repositoryRoot = repositoryRoot;
    }

    @Override
    public List<ToolInfo> requestTools(List<ToolConfiguration> requestedTools, File projectRoot) throws ToolsRepositoryException {
        try (LockFile lockFile = LockFileHelper.tryAcquireLockFile(getLockFile(), Duration.ofHours(1))) {
            ToolCatalogue catalogue = readCatalogue();
            List<ToolEntry> newEntries = new ArrayList<>();

            List<ToolInfo> toolDetails = new ArrayList<>();
            for (ToolConfiguration toolConfiguration : requestedTools) {
                ToolEntry entry = resolveExistingEntry(toolConfiguration, catalogue);
                if (entry != null) {
                    toolDetails.add(entry.getToolInstallationInfo());
                } else {
                    String toolId = generateToolId();
                    File toolRoot = resolveToolRoot(toolId);

                    installTool(toolConfiguration, toolRoot, projectRoot);
                    ToolInfo toolInfo = collectToolInfo(toolConfiguration, toolRoot, projectRoot);

                    newEntries.add(ToolEntryFactory.createToolEntry(toolId, toolConfiguration, toolInfo));

                    toolDetails.add(toolInfo);
                }
            }

            mergeAndWriteCatalogue(catalogue, newEntries);

            return toolDetails;
        } catch (IOException | TimeoutException | ProjectToolInstallerException e) {
            throw new ToolsRepositoryException("failed to request tools", e);
        }
    }

    @Override
    public void cleanAllToolsOfCurrentOSExcluding(List<ToolConfiguration> excludedTools) throws ToolsRepositoryException {
        try (LockFile lockFile = LockFileHelper.tryAcquireLockFile(getLockFile(), Duration.ofHours(1))) {
            OperatingSystem operatingSystem = OperatingSystem.getCurrentOS();

            List<ToolEntry> newCatalogueEntries = new ArrayList<>();
            for (ToolEntry catalogueEntry : readCatalogue().getEntries()) {
                if (operatingSystem != catalogueEntry.getTargetOS()) {
                    newCatalogueEntries.add(catalogueEntry);
                } else if (excludedTools.contains(catalogueEntry.getToolConfiguration())) {
                    newCatalogueEntries.add(catalogueEntry);
                } else {
                    FileUtils.forceDelete(resolveToolRoot(catalogueEntry));
                }
            }

            overwriteCatalogue(newCatalogueEntries);
        } catch (IOException | TimeoutException e) {
            throw new ToolsRepositoryException("failed to clean tools", e);
        }
    }

    private void installTool(ToolConfiguration toolConfiguration, File toolRoot, File projectRoot) throws ProjectToolInstallerException {
        ProjectToolInstallerContext installerContext = ImmutableProjectToolInstallerContext
                .builder()
                .projectRoot(projectRoot)
                .toolRoot(toolRoot)
                .build();

        ToolInstallers.installTool(toolConfiguration, installerContext);
    }

    private ToolInfo collectToolInfo(ToolConfiguration toolConfiguration, File toolRoot, File projectRoot) {
        ToolInfoCollectorContext collectorContext = ImmutableToolInfoCollectorContext
                .builder()
                .projectRoot(projectRoot)
                .toolRoot(toolRoot)
                .build();

        return ToolInfoCollectors.collectToolInfo(toolConfiguration, collectorContext);
    }

    private ToolEntry resolveExistingEntry(ToolConfiguration toolConfiguration, ToolCatalogue catalogue) {
        return catalogue
                .getEntries()
                .stream()
                .filter(entry -> CURRENT_OS == entry.getTargetOS() && Objects.equals(entry.getToolConfiguration(), toolConfiguration))
                .findFirst()
                .orElse(null);
    }

    private String generateToolId() {
        return UUID.randomUUID().toString();
    }

    private File resolveToolRoot(ToolEntry entry) {
        return new File(repositoryRoot, entry.getId());
    }

    private File resolveToolRoot(String toolId) {
        return new File(repositoryRoot, toolId);
    }

    private void mergeAndWriteCatalogue(ToolCatalogue catalogue, List<ToolEntry> entriesToAdd) throws IOException {
        ImmutableToolCatalogue mergedCatalogue = ImmutableToolCatalogue
                .builder()
                .from(catalogue)
                .addAllEntries(entriesToAdd)
                .build();

        YamlHelper.writeValue(mergedCatalogue, getCatalogueFile());
    }

    private void overwriteCatalogue(List<ToolEntry> newEntries) throws IOException {
        ImmutableToolCatalogue mergedCatalogue = ImmutableToolCatalogue
                .builder()
                .addAllEntries(newEntries)
                .build();

        YamlHelper.writeValue(mergedCatalogue, getCatalogueFile());
    }

    private ToolCatalogue readCatalogue() throws IOException {
        File catalogueFile = getCatalogueFile();
        if (catalogueFile.exists()) {
            return YamlHelper.readValue(getCatalogueFile(), ToolCatalogue.class);
        } else {
            return ImmutableToolCatalogue.builder().build();
        }
    }

    private File getCatalogueFile() {
        return new File(repositoryRoot, REPOSITORY_CATALOGUE);
    }

    private File getLockFile() {
        return new File(repositoryRoot, LOCK_FILE);
    }

}
