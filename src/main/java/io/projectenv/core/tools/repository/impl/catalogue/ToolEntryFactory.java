package io.projectenv.core.tools.repository.impl.catalogue;

import io.projectenv.core.configuration.*;
import io.projectenv.core.tools.info.*;

public final class ToolEntryFactory {

    private ToolEntryFactory() {

    }

    public static ToolEntry createFromBaseToolEntry(ToolEntry baseToolEntry) {
        ToolConfiguration toolConfiguration = baseToolEntry.getToolConfiguration();
        ToolInfo toolInfo = baseToolEntry.getToolInstallationInfo();

        if (toolConfiguration instanceof JdkConfiguration && toolInfo instanceof JdkInfo) {
            return ImmutableJdkEntry.builder().from(baseToolEntry)
                    .toolConfiguration((JdkConfiguration) baseToolEntry.getToolConfiguration())
                    .toolInstallationInfo((JdkInfo) baseToolEntry.getToolInstallationInfo())
                    .build();
        }

        if (toolConfiguration instanceof MavenConfiguration && toolInfo instanceof MavenInfo) {
            return ImmutableMavenEntry.builder().from(baseToolEntry)
                    .toolConfiguration((MavenConfiguration) baseToolEntry.getToolConfiguration())
                    .toolInstallationInfo((MavenInfo) baseToolEntry.getToolInstallationInfo())
                    .build();
        }

        if (toolConfiguration instanceof GradleConfiguration && toolInfo instanceof GradleInfo) {
            return ImmutableGradleEntry.builder().from(baseToolEntry)
                    .toolConfiguration((GradleConfiguration) baseToolEntry.getToolConfiguration())
                    .toolInstallationInfo((GradleInfo) baseToolEntry.getToolInstallationInfo())
                    .build();
        }

        if (toolConfiguration instanceof NodeConfiguration && toolInfo instanceof NodeInfo) {
            return ImmutableNodeEntry.builder().from(baseToolEntry)
                    .toolConfiguration((NodeConfiguration) baseToolEntry.getToolConfiguration())
                    .toolInstallationInfo((NodeInfo) baseToolEntry.getToolInstallationInfo())
                    .build();
        }

        if (toolConfiguration instanceof GenericToolConfiguration && toolInfo instanceof GenericToolInfo) {
            return ImmutableGenericToolEntry.builder().from(baseToolEntry)
                    .toolConfiguration((GenericToolConfiguration) baseToolEntry.getToolConfiguration())
                    .toolInstallationInfo((GenericToolInfo) baseToolEntry.getToolInstallationInfo())
                    .build();
        }

        if (toolConfiguration instanceof GitHooksConfiguration && toolInfo instanceof GitHooksInfo) {
            return ImmutableGitHooksEntry.builder().from(baseToolEntry)
                    .toolConfiguration((GitHooksConfiguration) baseToolEntry.getToolConfiguration())
                    .toolInstallationInfo((GitHooksInfo) baseToolEntry.getToolInstallationInfo())
                    .build();
        }

        throw new IllegalArgumentException("illegal combination of parameters");
    }

}
