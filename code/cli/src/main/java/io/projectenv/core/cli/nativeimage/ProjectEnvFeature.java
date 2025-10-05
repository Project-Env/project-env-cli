package io.projectenv.core.cli.nativeimage;

import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import io.projectenv.core.cli.shell.TemplateProcessor;
import io.projectenv.core.commons.process.ProcessOutput;
import io.projectenv.core.toolsupport.spi.ToolInfo;
import io.projectenv.core.toolsupport.spi.ToolSupport;
import org.apache.commons.compress.archivers.zip.ZipExtraField;
import org.graalvm.nativeimage.hosted.Feature;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.Reporter;
import org.slf4j.simple.SimpleLogger;

import java.io.IOException;
import java.util.Map;

import static io.projectenv.core.commons.nativeimage.NativeImageHelper.*;

public class ProjectEnvFeature implements Feature {

    private static final String BASE_PACKAGE = "io.projectenv";

    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        configureSlf4j();
        configureProcessOutputWriter();
        registerZipExtraFieldClasses();
        registerToolSupportService();
        registerGsonSupport();
        registerTemplates();
    }

    private void configureSlf4j() {
        initializeAtBuildTime(LoggerFactory.class);
        initializeAtBuildTime(SimpleLogger.class);
        initializeAtBuildTime(Reporter.class);
    }

    private void configureProcessOutputWriter() {
        initializeAtBuildTime(ProcessOutput.class);
    }

    private void registerZipExtraFieldClasses() {
        // Since Apache Commons Compress uses reflection to register the ZipExtraField
        // implementations, we have to register them for Reflection support.
        registerClassAndSubclassesForReflection(ZipExtraField.class);
    }

    private void registerToolSupportService() {
        registerService(ToolSupport.class);
    }

    private void registerGsonSupport() {
        registerService(TypeAdapterFactory.class);
        registerFieldsWithAnnotationForReflection(BASE_PACKAGE, SerializedName.class);
    }

    private void registerTemplates() {
        try {
            // Since it is possible to provide its own Pebble template,
            // we don't want to restrict the available String methods.
            registerClassForReflection(String.class);

            // register model classes for reflection
            registerClassForReflection(Map.class);
            registerClassAndSubclassesForReflection(ToolInfo.class);

            registerTemplate("sh.peb");
            registerTemplate("cygwin.peb");
            registerTemplate("pwsh.peb");
        } catch (IOException e) {
            throw new IllegalStateException("Failed to register templates for usage in native-image");
        }
    }

    private void registerTemplate(String templateName) throws IOException {
        registerResource(resolveTemplatePath(templateName));
    }

    private String resolveTemplatePath(String templateName) {
        return getTemplatesBasePath() + templateName;
    }

    private String getTemplatesBasePath() {
        return TemplateProcessor.class.getPackageName().replace(".", "/") + "/";
    }

}
