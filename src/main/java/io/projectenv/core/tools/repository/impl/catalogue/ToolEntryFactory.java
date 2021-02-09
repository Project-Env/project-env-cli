package io.projectenv.core.tools.repository.impl.catalogue;

import io.projectenv.core.common.OperatingSystem;
import io.projectenv.core.configuration.*;
import io.projectenv.core.tools.info.*;

public final class ToolEntryFactory {

    private ToolEntryFactory() {

    }

    public static ToolEntry createToolEntry(String toolId, ToolConfiguration toolConfiguration, ToolInfo toolInfo) {
        if (toolConfiguration instanceof JdkConfiguration && toolInfo instanceof JdkInfo) {
            return createJdkEntry(toolId, (JdkConfiguration) toolConfiguration, (JdkInfo) toolInfo);
        }

        if (toolConfiguration instanceof MavenConfiguration && toolInfo instanceof MavenInfo) {
            return createMavenEntry(toolId, (MavenConfiguration) toolConfiguration, (MavenInfo) toolInfo);
        }

        if (toolConfiguration instanceof GradleConfiguration && toolInfo instanceof GradleInfo) {
            return createGradleEntry(toolId, (GradleConfiguration) toolConfiguration, (GradleInfo) toolInfo);
        }

        if (toolConfiguration instanceof NodeConfiguration && toolInfo instanceof NodeInfo) {
            return createNodeEntry(toolId, (NodeConfiguration) toolConfiguration, (NodeInfo) toolInfo);
        }

        if (toolConfiguration instanceof GenericToolConfiguration && toolInfo instanceof GenericToolInfo) {
            return createGenericToolEntry(toolId, (GenericToolConfiguration) toolConfiguration, (GenericToolInfo) toolInfo);
        }

        throw new IllegalArgumentException("illegal combination of paramaters");
    }

    private static JdkEntry createJdkEntry(String toolId, JdkConfiguration toolConfiguration, JdkInfo toolInfo) {
        return ImmutableJdkEntry
                .builder()
                .id(toolId)
                .targetOS(OperatingSystem.getCurrentOS())
                .toolConfiguration(toolConfiguration)
                .toolInstallationInfo(toolInfo)
                .build();
    }

    private static MavenEntry createMavenEntry(String toolId, MavenConfiguration toolConfiguration, MavenInfo toolInfo) {
        return ImmutableMavenEntry
                .builder()
                .id(toolId)
                .targetOS(OperatingSystem.getCurrentOS())
                .toolConfiguration(toolConfiguration)
                .toolInstallationInfo(toolInfo)
                .build();
    }

    private static GradleEntry createGradleEntry(String toolId, GradleConfiguration toolConfiguration, GradleInfo toolInfo) {
        return ImmutableGradleEntry
                .builder()
                .id(toolId)
                .targetOS(OperatingSystem.getCurrentOS())
                .toolConfiguration(toolConfiguration)
                .toolInstallationInfo(toolInfo)
                .build();
    }

    private static NodeEntry createNodeEntry(String toolId, NodeConfiguration toolConfiguration, NodeInfo toolInfo) {
        return ImmutableNodeEntry
                .builder()
                .id(toolId)
                .targetOS(OperatingSystem.getCurrentOS())
                .toolConfiguration(toolConfiguration)
                .toolInstallationInfo(toolInfo)
                .build();
    }

    private static GenericToolEntry createGenericToolEntry(String toolId, GenericToolConfiguration toolConfiguration, GenericToolInfo toolInfo) {
        return ImmutableGenericToolEntry
                .builder()
                .id(toolId)
                .targetOS(OperatingSystem.getCurrentOS())
                .toolConfiguration(toolConfiguration)
                .toolInstallationInfo(toolInfo)
                .build();
    }

}
