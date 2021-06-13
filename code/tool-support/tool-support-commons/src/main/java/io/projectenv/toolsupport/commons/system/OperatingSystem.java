package io.projectenv.toolsupport.commons.system;

import org.apache.commons.lang3.SystemUtils;

public enum OperatingSystem {

    MACOS, WINDOWS, LINUX;

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