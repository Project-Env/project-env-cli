package io.projectenv.core.cli;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class ProjectEnvCliVersionHelper {

    public static final String VERSION_TXT = "version.txt";

    public static String getVersion() throws IOException {
        var url = ClassLoader.getSystemResource(VERSION_TXT);
        if (url == null) {
            return StringUtils.EMPTY;
        }

        return IOUtils.toString(url, StandardCharsets.UTF_8);
    }

}
