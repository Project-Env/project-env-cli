package io.projectenv.core.toolsupport.jdk.download.impl;

import io.projectenv.core.commons.system.CPUArchitecture;
import io.projectenv.core.commons.system.OperatingSystem;
import io.projectenv.core.toolsupport.jdk.JdkConfiguration;
import io.projectenv.core.toolsupport.jdk.download.JdkDownloadUrlResolver;
import io.projectenv.core.toolsupport.jdk.download.JdkDownloadUrlResolverException;
import io.projectenv.core.toolsupport.jdk.download.impl.discoapi.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

public class DiscoApiJdkDownloadUrlResolver implements JdkDownloadUrlResolver {

    private final DiscoApiClient discoApiClient;

    public DiscoApiJdkDownloadUrlResolver(DiscoApiClient discoApiClient) {
        this.discoApiClient = discoApiClient;
    }

    @Override
    public String resolveUrl(JdkConfiguration jdkConfiguration) throws JdkDownloadUrlResolverException {
        try {
            var distributionApiName = resolveDistributionApiName(jdkConfiguration);
            var ephemeralId = resolveEphemeralId(jdkConfiguration, distributionApiName);
            if (StringUtils.isEmpty(ephemeralId)) {
                throw createFailedResolutionOfJdkDownloadUrlException(jdkConfiguration);
            }

            var downloadUri = resolveDirectDownloadUri(ephemeralId);
            if (StringUtils.isEmpty(downloadUri)) {
                throw createFailedResolutionOfJdkDownloadUrlException(jdkConfiguration);
            }

            return downloadUri;
        } catch (Exception e) {
            throw createFailedResolutionOfJdkDownloadUrlException(jdkConfiguration, e);
        }
    }

    private String resolveDistributionApiName(JdkConfiguration jdkConfiguration) throws IOException {
        var result = discoApiClient.getDistributions(false, true);

        return Optional.ofNullable(result)
                .map(DiscoApiResult::getResult)
                .orElse(Collections.emptyList())
                .stream()
                .filter(distribution -> {
                    if (StringUtils.equalsIgnoreCase(distribution.getName(), jdkConfiguration.getDistribution())) {
                        return true;
                    }

                    for (String synonym : distribution.getSynonyms()) {
                        if (StringUtils.equalsIgnoreCase(synonym, jdkConfiguration.getDistribution())) {
                            return true;
                        }
                    }

                    return false;
                })
                .findFirst()
                .map(DiscoApiDistribution::getApiParameter)
                .orElse(jdkConfiguration.getDistribution());
    }

    private String resolveEphemeralId(JdkConfiguration jdkConfiguration, String distributionApiName) throws IOException {
        var result = discoApiClient.getJdkPackages(
                jdkConfiguration.getVersion(),
                distributionApiName,
                getCurrentCPUArchitecture(),
                getRequiredArchiveType(),
                getCurrentOperatingSystem());

        return Optional.ofNullable(result)
                .map(DiscoApiResult::getResult)
                .orElse(Collections.emptyList())
                .stream()
                .findFirst()
                .map(DiscoApiJdkPackage::getEphemeralId)
                .orElse(null);
    }

    private String getCurrentCPUArchitecture() {
        var currentCPUArchitecture = CPUArchitecture.getCurrentCPUArchitecture();
        if (currentCPUArchitecture == CPUArchitecture.X64) {
            return "x64";
        }

        throw new IllegalArgumentException("unsupported CPU architecture: " + currentCPUArchitecture);
    }

    private String getRequiredArchiveType() {
        var currentOperatingSystem = OperatingSystem.getCurrentOperatingSystem();
        if (currentOperatingSystem == OperatingSystem.MACOS || currentOperatingSystem == OperatingSystem.LINUX) {
            return "tar.gz";
        } else if (currentOperatingSystem == OperatingSystem.WINDOWS) {
            return "zip";
        }

        throw new IllegalArgumentException("unsupported OS: " + currentOperatingSystem);
    }

    private String getCurrentOperatingSystem() {
        return OperatingSystem.getCurrentOperatingSystem().name().toLowerCase();
    }

    private String resolveDirectDownloadUri(String ephemeralId) throws IOException {
        var result = discoApiClient.getJdkPackageDetails(ephemeralId);

        return Optional.ofNullable(result)
                .map(DiscoApiResult::getResult)
                .orElse(Collections.emptyList())
                .stream()
                .findFirst()
                .map(DiscoApiJdkPackageDetails::getDirectDownloadUri)
                .orElse(null);
    }

    private JdkDownloadUrlResolverException createFailedResolutionOfJdkDownloadUrlException(JdkConfiguration jdkConfiguration, Exception e) {
        return new JdkDownloadUrlResolverException(createFailedResolutionOfJdkDownloadUrlMessage(jdkConfiguration), e);
    }

    private JdkDownloadUrlResolverException createFailedResolutionOfJdkDownloadUrlException(JdkConfiguration jdkConfiguration) {
        return new JdkDownloadUrlResolverException(createFailedResolutionOfJdkDownloadUrlMessage(jdkConfiguration));
    }

    private String createFailedResolutionOfJdkDownloadUrlMessage(JdkConfiguration jdkConfiguration) {
        return "failed to resolve JDK download URL for version " + jdkConfiguration.getVersion() + " of distribution " +
                jdkConfiguration.getDistribution() + " through Disco API";
    }

}
