package io.projectenv.core.toolsupport.spi.index;

public interface ToolsIndexManager {

    String resolveMavenDistributionUrl(String version);

    String resolveGradleDistributionUrl(String version);

    String resolveNodeJsDistributionUrl(String version);

    String resolveJdkDistributionUrl(String jdkDistribution, String version);

}
