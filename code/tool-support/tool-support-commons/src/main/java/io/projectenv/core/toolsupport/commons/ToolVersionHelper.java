package io.projectenv.core.toolsupport.commons;

import com.vdurmont.semver4j.Semver;
import com.vdurmont.semver4j.Semver.SemverType;
import com.vdurmont.semver4j.Semver.VersionDiff;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class ToolVersionHelper {

    private ToolVersionHelper() {
        // noop
    }

    public static Optional<String> getNextToolVersion(String currentVersion, Set<String> allValidVersions) {
        UpgradeScope upgradeScope = getUpgradeScope(currentVersion);
        if (upgradeScope == null) {
            return Optional.empty();
        }

        String currentVersionWithoutPrefix = getVersionWithoutPrefix(currentVersion, upgradeScope);
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
                return Optional.of(upgradeScope.prefix + versionCandidate.getOriginalValue());
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

        return currentVersion.replace(upgradeScope.prefix, StringUtils.EMPTY);
    }

    private static UpgradeScope getUpgradeScope(String currentVersion) {
        for (UpgradeScope upgradeScope : UpgradeScope.values()) {
            if (currentVersion.startsWith(upgradeScope.prefix)) {
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

    private enum UpgradeScope {
        MAJOR("*"), MINOR("^"), PATCH("~");

        private final String prefix;

        UpgradeScope(String prefix) {
            this.prefix = prefix;
        }

    }

}
