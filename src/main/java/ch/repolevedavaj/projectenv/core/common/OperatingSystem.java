package ch.repolevedavaj.projectenv.core.common;

import org.apache.commons.lang3.SystemUtils;

public enum OperatingSystem {

    ALL, MACOS, WINDOWS, LINUX;

    public static OperatingSystem getCurrentOS() {
        if (SystemUtils.IS_OS_MAC) {
            return MACOS;
        }

        if (SystemUtils.IS_OS_LINUX) {
            return LINUX;
        }

        if (SystemUtils.IS_OS_WINDOWS) {
            return WINDOWS;
        }

        return null;
    }

}