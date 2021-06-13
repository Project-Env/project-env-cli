package io.projectenv.core.cli.nativeimage;

import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.oracle.svm.core.annotate.AutomaticFeature;
import io.projectenv.core.process.ProcessOutputWriterAccessor;
import io.projectenv.core.toolsupport.spi.ToolSupport;
import org.apache.commons.compress.archivers.zip.ZipExtraField;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeClassInitialization;

import java.io.IOException;

@AutomaticFeature
public class ProjectEnvFeature implements Feature {

    private static final String BASE_PACKAGE = "io.projectenv";

    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        configureProcessOutputWriter();
        registerZipExtraFieldClasses();
        registerToolSupportService();
        registerGsonSupport();
    }

    private void configureProcessOutputWriter() {
        RuntimeClassInitialization.initializeAtBuildTime(ProcessOutputWriterAccessor.class);
    }

    private void registerZipExtraFieldClasses() {
        // Since Apache Commons Compress uses reflection to register the ZipExtraField
        // implementations, we have to register them for Reflection support.
        NativeImageHelper.registerClassAndSubclassesForReflection(ZipExtraField.class);
    }

    private void registerToolSupportService() {
        try {
            NativeImageHelper.registerService(ToolSupport.class);
        } catch (IOException e) {
            throw new IllegalStateException("failed to register services for usage in native-image");
        }
    }

    private void registerGsonSupport() {
        try {
            NativeImageHelper.registerService(TypeAdapterFactory.class);
            NativeImageHelper.registerFieldsWithAnnotationForReflection(BASE_PACKAGE, SerializedName.class);
        } catch (IOException e) {
            throw new IllegalStateException("failed to register services for usage in native-image");
        }
    }

}
