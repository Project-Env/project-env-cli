package io.projectenv.core.cli.parser;

import com.google.gson.reflect.TypeToken;
import io.projectenv.commons.gson.GsonFactory;
import io.projectenv.core.toolsupport.spi.ToolInfo;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public final class ToolInfoParser {

    private static final Type TOOL_INFOS_TYPE = new TypeToken<Map<String, List<ToolInfo>>>() {
    }.getType();

    private ToolInfoParser() {
        // noop
    }

    public static Map<String, List<ToolInfo>> fromJson(String rawToolInfos) {
        return GsonFactory.createGson().fromJson(rawToolInfos, TOOL_INFOS_TYPE);
    }

    public static String toJson(Map<String, List<ToolInfo>> toolInfos) {
        return GsonFactory.createGson().toJson(toolInfos, TOOL_INFOS_TYPE);
    }


}
