package io.projectenv.core.toolsupport.spi;

public enum UpgradeScope {

    MAJOR("*"), MINOR("^"), PATCH("~");

    private final String prefix;

    UpgradeScope(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
