package io.projectenv.cli.nativeimage;

import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.oracle.svm.core.annotate.AutomaticFeature;
import io.projectenv.process.ProcessOutputWriterAccessor;
import io.projectenv.toolsupport.spi.ToolSupport;
import org.apache.commons.compress.archivers.zip.ZipExtraField;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeClassInitialization;

import java.io.IOException;

import static io.projectenv.cli.nativeimage.NativeImageHelper.*;

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
        registerClassAndSubclassesForReflection(ZipExtraField.class);
    }

    private void registerToolSupportService() {
        try {
            registerService(ToolSupport.class);
        } catch (IOException e) {
            throw new IllegalStateException("failed to register services for usage in native-image");
        }
    }

    private void registerGsonSupport() {
        try {
            registerService(TypeAdapterFactory.class);
            registerFieldsWithAnnotationForReflection(BASE_PACKAGE, SerializedName.class);
        } catch (IOException e) {
            throw new IllegalStateException("failed to register services for usage in native-image");
        }
    }

}
