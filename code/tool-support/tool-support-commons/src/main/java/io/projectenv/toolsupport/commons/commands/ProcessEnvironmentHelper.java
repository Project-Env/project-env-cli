package io.projectenv.toolsupport.commons.commands;

import io.projectenv.toolsupport.commons.system.OperatingSystem;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public final class ProcessEnvironmentHelper {

    private static final String PATH_ENVIRONMENT_VARIABLE = "PATH";

    private static final Map<OperatingSystem, List<String>> OS_EXECUTABLE_EXTENSIONS = Map.of(
            OperatingSystem.WINDOWS, List.of(".exe", ".cmd")
    );

    private ProcessEnvironmentHelper() {
        // noop
    }

    public static Map<String, String> createProcessEnvironmentFromToolInfo(Map<String, File> environmentVariables, List<File> pathElements) throws IOException {
        var processEnvironment = environmentVariables
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getAbsolutePath()));

        var pathExtension = new StringJoiner(":");
        for (var file : pathElements) {
            pathExtension.add(file.getCanonicalPath());
        }

        processEnvironment.put(PATH_ENVIRONMENT_VARIABLE, pathExtension.toString() + File.pathSeparator + System.getenv(PATH_ENVIRONMENT_VARIABLE));

        return processEnvironment;
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

}
