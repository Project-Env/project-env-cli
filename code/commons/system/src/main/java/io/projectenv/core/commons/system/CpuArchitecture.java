package io.projectenv.core.commons.system;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.util.Arrays;

public enum CpuArchitecture {

    @SerializedName("amd64")
    AMD64("amd64", "x86_64"),
    @SerializedName("aarch64")
    AARCH64("aarch64");

    private final String[] identifiers;

    CpuArchitecture(String... identifiers) {
        this.identifiers = identifiers;
    }

    public static CpuArchitecture getCurrentCpuArchitecture() {
        return Arrays.stream(values())
                .filter(value -> Arrays
                        .stream(value.identifiers)
                        .anyMatch(identifier -> StringUtils.equalsIgnoreCase(SystemUtils.OS_ARCH, identifier)))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("unsupported CPU architecture " + SystemUtils.OS_ARCH));
    }


}
