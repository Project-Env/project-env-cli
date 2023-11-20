package io.projectenv.core.toolsupport.commons;

import io.projectenv.core.toolsupport.spi.UpgradeScope;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.semver4j.Semver;
import org.semver4j.Semver.VersionDiff;

import java.util.*;

public final class ToolVersionHelper {

    private ToolVersionHelper() {
        // noop
    }

    public static Optional<String> getNextToolVersion(String currentVersion, UpgradeScope upgradeScope, Set<String> allValidVersions) {
        UpgradeScope currentUpgradeScope = getUpgradeScope(currentVersion);
        String currentVersionWithoutPrefix = getVersionWithoutPrefix(currentVersion, currentUpgradeScope);
        Semver currentVersionAsSemver = Semver.coerce(currentVersionWithoutPrefix);

        List<ImmutablePair<String, Semver>> allValidVersionsAsSemver = allValidVersions
                .stream()
                .map(version -> ImmutablePair.of(version, Semver.coerce(version)))
                .sorted(Collections.reverseOrder(Comparator.comparing(Pair::getRight)))
                .toList();

        Set<VersionDiff> validDiffs = getValidDiffs(upgradeScope);
        for (ImmutablePair<String, Semver> versionCandidate : allValidVersionsAsSemver) {
            if (versionCandidate.getRight().equals(currentVersionAsSemver)) {
                break;
            }

            if (validDiffs.contains(currentVersionAsSemver.diff(versionCandidate.getRight()))) {
                String prefix = Optional.ofNullable(currentUpgradeScope).map(UpgradeScope::getPrefix).orElse("");

                return Optional.of(prefix + versionCandidate.getLeft());
            }
        }

        return Optional.empty();
    }

    public static String getVersionWithoutPrefix(String currentVersion) {
        return getVersionWithoutPrefix(currentVersion, getUpgradeScope(currentVersion));
    }

    private static String getVersionWithoutPrefix(String currentVersion, UpgradeScope upgradeScope) {
        if (upgradeScope == null) {
            return currentVersion;
        }

        return currentVersion.replace(upgradeScope.getPrefix(), StringUtils.EMPTY);
    }

    public static UpgradeScope getUpgradeScope(String currentVersion) {
        for (UpgradeScope upgradeScope : UpgradeScope.values()) {
            if (currentVersion.startsWith(upgradeScope.getPrefix())) {
                return upgradeScope;
            }
        }

        return null;
    }

    private static Set<VersionDiff> getValidDiffs(UpgradeScope upgradeScope) {
        return switch (upgradeScope) {
            case MAJOR -> Set.of(VersionDiff.MAJOR, VersionDiff.MINOR, VersionDiff.PATCH);
            case MINOR -> Set.of(VersionDiff.MINOR, VersionDiff.PATCH);
            case PATCH -> Set.of(VersionDiff.PATCH);
        };
    }

}
