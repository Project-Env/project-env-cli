package io.projectenv.core.commons.download;

import io.projectenv.core.commons.download.impl.DownloadUrlVariableLookup;
import org.apache.commons.text.StringSubstitutor;

public final class DownloadUrlSubstitutorFactory {

    private DownloadUrlSubstitutorFactory() {
        // noop
    }

    public static StringSubstitutor createDownloadUrlVariableSubstitutor(DownloadUrlDictionary dictionary) {
        return new StringSubstitutor(new DownloadUrlVariableLookup(dictionary))
                .setEnableUndefinedVariableException(true);
    }

}
