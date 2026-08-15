package io.projectenv.core.cli.shell;

import io.projectenv.core.toolsupport.spi.ImmutableToolInfo;
import io.projectenv.core.toolsupport.spi.ToolInfo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TemplateProcessorTest {

    private static final File MAVEN_HOME = new File("/tools/maven");
    private static final File MAVEN_BINARIES = new File("/tools/maven/bin");
    private static final File MAVEN_USER_SETTINGS = new File("/project/etc/m2/settings.xml");

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

    @Test
    void testShTemplateExposesMavenSettingsAsFunction() throws Exception {
        var actualContent = TemplateProcessor.processTemplate("sh", mavenToolInfos(), false);

        // A function, not an alias: an alias is silently ignored by any non-interactive
        // shell, so the settings file would be dropped in CI and in `sh -c`.
        assertThat(actualContent).contains("mvn() {");
        assertThat(actualContent).contains("command mvn -s \"" + renderedPath(MAVEN_USER_SETTINGS) + "\" \"$@\"");
        assertThat(actualContent).doesNotContain("alias mvn");
    }

    @Test
    void testShTemplateLeavesPathsUntouchedOffWindows() throws Exception {
        var actualContent = TemplateProcessor.processTemplate("sh", mavenToolInfos(), false);

        assertThat(actualContent).contains("export MAVEN_HOME=\"" + renderedPath(MAVEN_HOME) + "\"");
        assertThat(actualContent).contains("export PATH=\"" + renderedPath(MAVEN_BINARIES) + ":$PATH\"");
        assertThat(actualContent).doesNotContain("cygpath");
    }

    @Test
    void testShTemplateConvertsPathsOnWindows() throws Exception {
        var actualContent = TemplateProcessor.processTemplate("sh", mavenToolInfos(), true);

        // A POSIX shell on Windows is Cygwin or MSYS2, where ':' separates PATH entries.
        // Without the conversion, 'C:/tools/maven/bin' would split into 'C' and
        // '/tools/maven/bin', leaving a dead entry.
        assertThat(actualContent).contains("export MAVEN_HOME=\"$(cygpath '" + renderedPath(MAVEN_HOME) + "')\"");
        assertThat(actualContent).contains("export PATH=\"$(cygpath '" + renderedPath(MAVEN_BINARIES) + "'):$PATH\"");
    }

    @Test
    void testCygwinTemplateMatchesTheWindowsRenderingOfSh() throws Exception {
        var shOnWindows = TemplateProcessor.processTemplate("sh", mavenToolInfos(), true);
        var cygwin = TemplateProcessor.processTemplate("cygwin", mavenToolInfos(), true);

        assertThat(withoutBlankLines(cygwin)).isEqualTo(withoutBlankLines(shOnWindows));
    }

    /**
     * The two templates differ only in how many blank lines their control tags leave
     * behind, which says nothing about the script they produce.
     */
    private String withoutBlankLines(String script) {
        return script.lines().filter(StringUtils::isNotBlank).reduce("", (a, b) -> a + b + "\n");
    }

    private Map<String, List<ToolInfo>> mavenToolInfos() {
        var toolInfo = ImmutableToolInfo.builder()
                .putEnvironmentVariables("MAVEN_HOME", MAVEN_HOME)
                .addPathElements(MAVEN_BINARIES)
                .putUnhandledProjectResources("userSettingsFile", MAVEN_USER_SETTINGS)
                .build();

        return Map.of("maven", List.of(toolInfo));
    }

    /**
     * The templates render a file through the {@code path} filter, which makes the path canonical
     * and turns back-slashes into forward-slashes. A path such as {@code /tools/maven} is not
     * absolute on Windows, where it gains the drive of the working directory, so the expected
     * value is derived the same way instead of being written out.
     */
    private String renderedPath(File file) throws IOException {
        return file.getCanonicalPath().replace('\\', '/');
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