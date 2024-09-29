package io.projectenv.core.cli.shell;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TemplateProcessorTest {

    @Test
    void testFileTemplate(@TempDir File tempDir) throws Exception {
        var expectedContent = "custom-template";

        var customTemplate = createTemplateInDirectory(expectedContent, tempDir);
        var actualContent = TemplateProcessor.processTemplate(customTemplate, Map.of());
        assertThat(actualContent).isEqualTo(expectedContent);
    }

    @Test
    void testClasspathTemplateWithExtension() throws Exception {
        var expectedContent = "custom-template";

        var customTemplate = createTemplateInTemplatesClasspathDirectory(expectedContent);
        assertThat(customTemplate).endsWith(TemplateProcessor.PEBBLE_TEMPLATE_EXT);

        var actualContent = TemplateProcessor.processTemplate(customTemplate, Map.of());
        assertThat(actualContent).isEqualTo(expectedContent);
    }

    @Test
    void testClasspathTemplateWithoutExtension() throws Exception {
        var expectedContent = "custom-template";

        var customTemplate = createTemplateInTemplatesClasspathDirectory(expectedContent);
        assertThat(customTemplate).endsWith(TemplateProcessor.PEBBLE_TEMPLATE_EXT);

        customTemplate = StringUtils.remove(customTemplate, TemplateProcessor.PEBBLE_TEMPLATE_EXT);
        assertThat(customTemplate).doesNotEndWith(TemplateProcessor.PEBBLE_TEMPLATE_EXT);

        var actualContent = TemplateProcessor.processTemplate(customTemplate, Map.of());
        assertThat(actualContent).isEqualTo(expectedContent);
    }

    private String createTemplateInDirectory(String templateContent, File parentDirectory) throws Exception {
        var customTemplate = File.createTempFile("custom-template", ".peb", parentDirectory);
        FileUtils.write(customTemplate, templateContent, StandardCharsets.UTF_8);

        return customTemplate.getAbsolutePath();
    }

    private String createTemplateInTemplatesClasspathDirectory(String templateContent) throws Exception {
        var customTemplate = File.createTempFile("custom-template", ".peb", getTemplatesClasspathLocation());
        FileUtils.write(customTemplate, templateContent, StandardCharsets.UTF_8);

        return customTemplate.getName();
    }

    private File getTemplatesClasspathLocation() throws Exception {
        return new File(getClass().getResource(".").toURI());
    }

}