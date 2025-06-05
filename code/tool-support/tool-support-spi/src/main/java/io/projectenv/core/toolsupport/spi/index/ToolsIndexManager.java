package io.projectenv.core.toolsupport.spi.index;

import java.util.Set;

public interface ToolsIndexManager {

    String resolveMavenDistributionUrl(String version);

    Set<String> getMavenVersions();

    String resolveGradleDistributionUrl(String version);

    Set<String> getGradleVersions();

    String resolveNodeJsDistributionUrl(String version);

    Set<String> getNodeJsVersions();

    String resolveJdkDistributionUrl(String jdkDistribution, String version);

    Set<String> getJdkDistributionVersions(String jdkDistribution);

    String resolveClojureDistributionUrl(String version);

    Set<String> getClojureVersions();

}
