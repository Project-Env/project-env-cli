package ch.repolevedavaj.projectenv.core.os;

import org.apache.commons.lang3.SystemUtils;

public enum OS {

    MACOS, WINDOWS, LINUX, UNKNOWN;

    public static OS getCurrentOS() {
        if (SystemUtils.IS_OS_MAC) {
            return MACOS;
        }

        if (SystemUtils.IS_OS_LINUX) {
            return LINUX;
        }

        if (SystemUtils.IS_OS_WINDOWS) {
            return WINDOWS;
        }

        return UNKNOWN;
    }

}
