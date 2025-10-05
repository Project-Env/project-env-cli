package io.projectenv.core.toolsupport.commons;

import io.projectenv.core.toolsupport.spi.*;

import java.util.Optional;
import java.util.Set;

public abstract class AbstractUpgradableToolSupport<T> implements ToolSupport<T> {

    @Override
    public Optional<ToolUpgradeInfo> upgradeToolVersion(T toolConfiguration, ToolSupportContext context) {
        UpgradeScope upgradeScope = ToolVersionHelper.getUpgradeScope(getCurrentVersion(toolConfiguration));
        if (upgradeScope == null) {
            return Optional.empty();
        }


        return upgradeToolVersion(toolConfiguration, upgradeScope, context);
    }

    @Override
    public Optional<ToolUpgradeInfo> upgradeToolVersion(T toolConfiguration, UpgradeScope upgradeScope, ToolSupportContext context) {
        String currentVersion = getCurrentVersion(toolConfiguration);

        return ToolVersionHelper.getNextToolVersion(currentVersion, upgradeScope, getAllValidVersions(toolConfiguration, context))
                .map(nextToolVersion -> ImmutableToolUpgradeInfo.builder()
                        .currentVersion(currentVersion)
                        .upgradedVersion(nextToolVersion)
                        .build());
    }

    protected abstract String getCurrentVersion(T toolConfiguration);

    protected abstract Set<String> getAllValidVersions(T toolConfiguration, ToolSupportContext context);

    public String getName(T toolConfiguration) {
        return getToolIdentifier();
    }

    @Override
    public String getDescription(T toolConfiguration) {
		return getName(toolConfiguration) + " " + ToolVersionHelper.getVersionWithoutPrefix(getCurrentVersion(toolConfiguration));
    }
}
