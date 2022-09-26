package io.projectenv.core.toolsupport.commons;

import io.projectenv.core.toolsupport.spi.ImmutableToolUpgradeInfo;
import io.projectenv.core.toolsupport.spi.ToolSupport;
import io.projectenv.core.toolsupport.spi.ToolSupportContext;
import io.projectenv.core.toolsupport.spi.ToolUpgradeInfo;

import java.util.Optional;
import java.util.Set;

public abstract class AbstractUpgradableToolSupport<T> implements ToolSupport<T> {

    @Override
    public Optional<ToolUpgradeInfo> upgradeToolVersion(T toolConfiguration, ToolSupportContext context) {
        String currentVersion = getCurrentVersion(toolConfiguration);

        return ToolVersionHelper.getNextToolVersion(currentVersion, getAllValidVersions(toolConfiguration, context))
                .map(nextToolVersion -> ImmutableToolUpgradeInfo.builder()
                        .currentVersion(currentVersion)
                        .upgradedVersion(nextToolVersion)
                        .build());
    }

    protected abstract String getCurrentVersion(T toolConfiguration);

    protected abstract Set<String> getAllValidVersions(T toolConfiguration, ToolSupportContext context);

}
