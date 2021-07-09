package io.projectenv.core.commons.process;

import io.projectenv.core.commons.system.OperatingSystem;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public final class ProcessEnvironmentHelper {

    private static final List<String> PATH_VARIABLE_NAME_CANDIDATES = List.of("PATH", "Path");

    private static final Map<OperatingSystem, List<String>> OS_EXECUTABLE_EXTENSIONS = Map.of(
            OperatingSystem.WINDOWS, List.of(".exe", ".cmd")
    );

    private ProcessEnvironmentHelper() {
        // noop
    }

    public static Map<String, String> createProcessEnvironment(Map<String, File> environmentVariables, List<File> pathElements) {
        Map<String, String> processEnvironment = new HashMap<>(System.getenv());

        extendWithEnvironmentVariables(processEnvironment, environmentVariables);
        extendPathWithElements(processEnvironment, pathElements);

        return processEnvironment;
    }

    public static void extendWithEnvironmentVariables(Map<String, String> env, Map<String, File> environmentVariables) {
        for (Map.Entry<String, File> entry : environmentVariables.entrySet()) {
            env.put(entry.getKey(), entry.getValue().getAbsolutePath());
        }
    }

    public static void extendPathWithElements(Map<String, String> env, List<File> pathExtensions) {
        env.put(getPathVariableName(), createExtendedPathValue(pathExtensions));
    }

    public static String createExtendedPathValue(File... pathExtensions) {
        return createExtendedPathValue(Arrays.asList(pathExtensions));
    }

    public static String createExtendedPathValue(List<File> pathExtensions) {
        var pathExtension = new StringJoiner(File.pathSeparator);
        for (var file : pathExtensions) {
            pathExtension.add(file.getAbsolutePath());
        }

        return pathExtension + File.pathSeparator + getPathVariableContent();
    }

    public static File resolveExecutableFromPath(String executable) {
        return resolveExecutableFromPathElements(executable, getPathElements());
    }

    public static File resolveExecutableFromPathElements(String executable, List<File> pathElements) {
        var possibleExtensions = new ArrayList<>();

        if (OS_EXECUTABLE_EXTENSIONS.containsKey(OperatingSystem.getCurrentOperatingSystem())) {
            possibleExtensions.addAll(OS_EXECUTABLE_EXTENSIONS.get(OperatingSystem.getCurrentOperatingSystem()));
        }
        possibleExtensions.add(StringUtils.EMPTY);

        for (var possibleExtension : possibleExtensions) {
            var executableWithExtension = executable + possibleExtension;

            for (var pathElement : pathElements) {
                var executableCandidate = new File(pathElement, executableWithExtension);
                if (executableCandidate.exists()) {
                    return executableCandidate;
                }
            }
        }

        return null;
    }

    public static List<File> getPathElements() {
        var pathVariableContent = getPathVariableContent();

        return Arrays.stream(StringUtils.split(pathVariableContent, File.pathSeparator))
                .map(File::new)
                .collect(Collectors.toList());
    }

    public static String getPathVariableContent() {
        return System.getenv(getPathVariableName());
    }

    public static String getPathVariableName() {
        for (String pathVariableName : PATH_VARIABLE_NAME_CANDIDATES) {
            if (System.getenv().containsKey(pathVariableName)) {
                return pathVariableName;
            }
        }

        throw new IllegalStateException("could not resolve path variable name");
    }

}
