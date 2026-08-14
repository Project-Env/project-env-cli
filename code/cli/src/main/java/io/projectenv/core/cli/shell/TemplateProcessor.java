package io.projectenv.core.cli.shell;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.extension.AbstractExtension;
import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.loader.ClasspathLoader;
import io.pebbletemplates.pebble.loader.FileLoader;
import io.pebbletemplates.pebble.loader.Loader;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import io.projectenv.core.commons.system.OperatingSystem;
import io.projectenv.core.toolsupport.spi.ToolInfo;
import org.apache.commons.lang3.ClassPathUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public final class TemplateProcessor {

    public static final String PEBBLE_TEMPLATE_EXT = ".peb";

    private static final PebbleEngine CLASSPATH_TEMPLATE_ENGINE = createEngine(new ClasspathLoader());

    private TemplateProcessor() {
        // noop
    }

    public static String processTemplate(String template, Map<String, List<ToolInfo>> toolInfos) throws IOException {
        return processTemplate(template, toolInfos, OperatingSystem.getCurrentOperatingSystem() == OperatingSystem.WINDOWS);
    }

    /**
     * Renders a template with an explicit target operating system.
     * <p>
     * Only the OS decides whether a POSIX shell needs its paths converted: on Windows
     * a POSIX shell is Cygwin or MSYS2, where {@code :} separates PATH entries, so a
     * path such as {@code C:/x/bin} would split in two. The conversion is left to
     * {@code cygpath} at runtime rather than done here, because the two environments
     * disagree on the result ({@code /cygdrive/c/x/bin} vs {@code /c/x/bin}).
     *
     * @param windows whether the rendered script will run on Windows
     */
    static String processTemplate(String template, Map<String, List<ToolInfo>> toolInfos, boolean windows) throws IOException {
        PebbleTemplate compiledTemplate = compileTemplate(template);

        Writer writer = new StringWriter();

        var context = new HashMap<String, Object>();
        context.put("toolInfos", toolInfos);
        context.put("windows", windows);

        compiledTemplate.evaluate(writer, context);

        return writer.toString();
    }

    /**
     * A template is either a custom one the user points to by file path or a built-in
     * one on the classpath. Pebble's file loader reads inside one base directory only
     * and rejects absolute template names, so a file template gets its own engine
     * anchored at the directory of that file.
     */
    private static PebbleTemplate compileTemplate(String template) {
        var templateFile = new File(template).getAbsoluteFile();
        if (templateFile.isFile()) {
            return createEngine(new FileLoader(templateFile.getParent())).getTemplate(templateFile.getName());
        }

        return CLASSPATH_TEMPLATE_ENGINE.getTemplate(resolveClasspathTemplate(template));
    }

    private static PebbleEngine createEngine(Loader<?> loader) {
        return new PebbleEngine
                .Builder()
                .strictVariables(false)
                .extension(new PebbleExtension())
                .loader(loader)
                .build();
    }

    private static String resolveClasspathTemplate(String template) {
        if (StringUtils.endsWith(template, PEBBLE_TEMPLATE_EXT)) {
            return ClassPathUtils.toFullyQualifiedPath(TemplateProcessor.class, template);
        }

        return ClassPathUtils.toFullyQualifiedPath(TemplateProcessor.class, template + PEBBLE_TEMPLATE_EXT);
    }

    private static class PebbleExtension extends AbstractExtension {

        @Override
        public Map<String, Filter> getFilters() {
            return Map.of("path", new PathFilter());
        }

    }

    private static class PathFilter implements Filter {

        @Override
        public List<String> getArgumentNames() {
            return Collections.emptyList();
        }

        @Override
        public Object apply(Object input, Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) {
            try {
                if (input instanceof File file) {
                    String canonicalPath = file.getCanonicalPath();

                    // removes a trailing path separator if existing
                    canonicalPath = canonicalPath.replaceAll(Pattern.quote(File.separator) + "$", "");

                    // replaces all back-slashes with forward-slashes
                    canonicalPath = canonicalPath.replaceAll(Pattern.quote("\\"), "/");

                    return canonicalPath;
                } else {
                    return input;
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("invalid file", e);
            }
        }

    }

}
