package io.projectenv.core.toolsupport.maven;

import java.text.MessageFormat;

public final class MavenDownloadUrlResolver {

    private MavenDownloadUrlResolver() {
        // noop
    }

    public static String resolveUrl(String version) {
        return MessageFormat.format("https://archive.apache.org/dist/maven/maven-3/{0}/binaries/apache-maven-{0}-bin.tar.gz", version);
    }

}
