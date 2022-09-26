package io.projectenv.core.toolsupport.spi;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface ToolUpgradeInfo {

    String getCurrentVersion();

    String getUpgradedVersion();

}
