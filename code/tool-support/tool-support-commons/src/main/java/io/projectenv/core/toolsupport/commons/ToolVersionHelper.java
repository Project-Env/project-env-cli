package io.projectenv.core.toolsupport.commons;

import com.vdurmont.semver4j.Semver;
import com.vdurmont.semver4j.Semver.SemverType;
import com.vdurmont.semver4j.Semver.VersionDiff;
import io.projectenv.core.toolsupport.spi.UpgradeScope;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class ToolVersionHelper {

    private ToolVersionHelper() {
        // noop
    }

    public static Optional<String> getNextToolVersion(String currentVersion, UpgradeScope upgradeScope, Set<String> allValidVersions) {
        UpgradeScope currentUpgradeScope = getUpgradeScope(currentVersion);
        String currentVersionWithoutPrefix = getVersionWithoutPrefix(currentVersion, currentUpgradeScope);
        Semver currentVersionAsSemver = new Semver(currentVersionWithoutPrefix, SemverType.LOOSE);

        List<Semver> allValidVersionsAsSemver = allValidVersions
                .stream()
                .map(value -> new Semver(value, SemverType.LOOSE))
                .sorted(Comparator.<Semver>naturalOrder().reversed())
                .toList();

        Set<VersionDiff> validDiffs = getValidDiffs(upgradeScope);
        for (Semver versionCandidate : allValidVersionsAsSemver) {
            if (versionCandidate.equals(currentVersionAsSemver)) {
                break;
            }

            if (validDiffs.contains(currentVersionAsSemver.diff(versionCandidate))) {
                String prefix = Optional.ofNullable(currentUpgradeScope).map(UpgradeScope::getPrefix).orElse("");

                return Optional.of(prefix + versionCandidate.getOriginalValue());
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
