package io.projectenv.core.toolsupport.jdk.download.impl;

import io.projectenv.core.commons.download.DownloadUrlDictionary;
import io.projectenv.core.commons.download.DownloadUrlSubstitutorFactory;
import io.projectenv.core.toolsupport.jdk.JdkConfiguration;
import io.projectenv.core.toolsupport.jdk.download.JdkDownloadUrlResolverStrategy;

public abstract class AbstractJdkDownloadUrlResolverStrategy implements JdkDownloadUrlResolverStrategy {

    @Override
    public String resolveUrl(JdkConfiguration jdkConfiguration) {
        var downloadUrlPattern = getDownloadUrlPattern(jdkConfiguration);
        var downloadUrlDictionary = getDownloadUrlDictionary(jdkConfiguration);

        return DownloadUrlSubstitutorFactory
                .createDownloadUrlVariableSubstitutor(downloadUrlDictionary)
                .replace(downloadUrlPattern);
    }

    protected abstract String getDownloadUrlPattern(JdkConfiguration jdkConfiguration);

    protected abstract DownloadUrlDictionary getDownloadUrlDictionary(JdkConfiguration jdkConfiguration);

}
