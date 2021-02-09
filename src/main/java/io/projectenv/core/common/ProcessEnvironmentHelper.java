package io.projectenv.core.common;

import io.projectenv.core.tools.info.ToolInfo;
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

    public static Map<String, String> createProcessEnvironmentFromToolInfo(ToolInfo toolInfo) throws IOException {
        Map<String, String> processEnvironment = toolInfo.getEnvironmentVariables()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getAbsolutePath()));

        StringJoiner pathExtension = new StringJoiner(":");
        for (File file : toolInfo.getPathElements()) {
            pathExtension.add(file.getCanonicalPath());
        }

        processEnvironment.put(PATH_ENVIRONMENT_VARIABLE, pathExtension.toString() + File.pathSeparator + System.getenv(PATH_ENVIRONMENT_VARIABLE));

        return processEnvironment;
    }

    public static File resolveExecutableFromToolInfo(String executable, ToolInfo toolInfo) {
        return resolveExecutableFromPathElements(executable, toolInfo.getPathElements());
    }

    public static File resolveExecutableFromPathElements(String executable, List<File> pathElements) {
        List<String> possibleExtensions = new ArrayList<>();

        if (OS_EXECUTABLE_EXTENSIONS.containsKey(OperatingSystem.getCurrentOS())) {
            possibleExtensions.addAll(OS_EXECUTABLE_EXTENSIONS.get(OperatingSystem.getCurrentOS()));
        }
        possibleExtensions.add(StringUtils.EMPTY);

        for (String possibleExtension : possibleExtensions) {
            String executableWithExtension = executable + possibleExtension;

            for (File pathElement : pathElements) {
                File executableCandidate = new File(pathElement, executableWithExtension);
                if (executableCandidate.exists()) {
                    return executableCandidate;
                }
            }
        }

        return null;
    }

}
