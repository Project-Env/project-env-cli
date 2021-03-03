package io.projectenv.core.tools.repository.impl;

import io.projectenv.core.common.OperatingSystem;
import io.projectenv.core.common.YamlHelper;
import io.projectenv.core.common.lock.LockFile;
import io.projectenv.core.common.lock.LockFileHelper;
import io.projectenv.core.configuration.ToolConfiguration;
import io.projectenv.core.tools.info.ToolInfo;
import io.projectenv.core.tools.repository.ToolsRepository;
import io.projectenv.core.tools.repository.ToolsRepositoryException;
import io.projectenv.core.tools.repository.impl.catalogue.*;
import io.projectenv.core.tools.service.ImmutableToolSpecificServiceContext;
import io.projectenv.core.tools.service.ToolSpecificServiceContext;
import io.projectenv.core.tools.service.ToolSpecificServices;
import io.projectenv.core.tools.service.installer.ToolInstallerException;
import io.projectenv.core.tools.service.resources.LocalToolResourcesProcessorException;
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
                    ToolSpecificServiceContext context = ImmutableToolSpecificServiceContext
                            .builder()
                            .projectRoot(projectRoot)
                            .toolRoot(resolveToolRoot(entry.getId()))
                            .build();

                    ToolSpecificServices.processLocalToolResources(entry.getToolInstallationInfo(), context);
                    toolDetails.add(entry.getToolInstallationInfo());
                } else {
                    String toolId = generateToolId();

                    ToolSpecificServiceContext context = ImmutableToolSpecificServiceContext
                            .builder()
                            .projectRoot(projectRoot)
                            .toolRoot(resolveToolRoot(toolId))
                            .build();

                    ToolSpecificServices.installTool(toolConfiguration, context);
                    ToolInfo toolInfo = ToolSpecificServices.collectToolInfo(toolConfiguration, context);
                    ToolSpecificServices.processLocalToolResources(toolInfo, context);

                    ToolEntry baseEntry = ImmutableToolEntry
                            .builder()
                            .id(toolId)
                            .toolConfiguration(toolConfiguration)
                            .toolInstallationInfo(toolInfo)
                            .targetOS(OperatingSystem.getCurrentOS())
                            .build();
                    newEntries.add(ToolEntryFactory.createFromBaseToolEntry(baseEntry));

                    toolDetails.add(toolInfo);
                }
            }

            mergeAndWriteCatalogue(catalogue, newEntries);

            return toolDetails;
        } catch (IOException | TimeoutException | ToolInstallerException | LocalToolResourcesProcessorException e) {
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
        ToolCatalogue mergedCatalogue = ImmutableToolCatalogue
                .builder()
                .from(catalogue)
                .addAllEntries(entriesToAdd)
                .build();

        YamlHelper.writeValue(mergedCatalogue, getCatalogueFile());
    }

    private void overwriteCatalogue(List<ToolEntry> newEntries) throws IOException {
        ToolCatalogue mergedCatalogue = ImmutableToolCatalogue
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
