package io.projectenv.core.commons.system;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.SystemUtils;

public enum OperatingSystem {

    @SerializedName("macos")
    MACOS,
    @SerializedName("windows")
    WINDOWS,
    @SerializedName("linux")
    LINUX;

    public static OperatingSystem getCurrentOperatingSystem() {
        if (SystemUtils.IS_OS_MAC) {
            return MACOS;
        }

        if (SystemUtils.IS_OS_LINUX) {
            return LINUX;
        }

        if (SystemUtils.IS_OS_WINDOWS) {
            return WINDOWS;
        }

        throw new IllegalStateException("unsupported operating system " + SystemUtils.OS_NAME);
    }

}