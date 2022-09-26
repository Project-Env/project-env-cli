package io.projectenv.core.cli.parser;

import com.google.gson.reflect.TypeToken;
import io.projectenv.commons.gson.GsonFactory;
import io.projectenv.core.toolsupport.spi.ToolInfo;
import io.projectenv.core.toolsupport.spi.ToolUpgradeInfo;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public final class ToolUpgradeInfoParser {

    private static final Type TOOL_UPGRADE_INFOS_TYPE = new TypeToken<Map<String, List<ToolUpgradeInfo>>>() {
    }.getType();

    private ToolUpgradeInfoParser() {
        // noop
    }

    public static Map<String, List<ToolInfo>> fromJson(String rawTToolUpgradeInfos) {
        return GsonFactory.createGson().fromJson(rawTToolUpgradeInfos, TOOL_UPGRADE_INFOS_TYPE);
    }

    public static String toJson(Map<String, List<ToolUpgradeInfo>> toolUpgradeInfos) {
        return GsonFactory.createGson().toJson(toolUpgradeInfos, TOOL_UPGRADE_INFOS_TYPE);
    }

}
