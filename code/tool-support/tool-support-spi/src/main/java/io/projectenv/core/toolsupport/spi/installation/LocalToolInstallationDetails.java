package io.projectenv.core.toolsupport.spi.installation;

import org.apache.commons.lang3.tuple.Pair;
import org.immutables.value.Value;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Value.Immutable
public interface LocalToolInstallationDetails {

    Optional<File> getBinariesRoot();

    Optional<File> getPrimaryExecutable();

    List<File> getPathElements();

    Map<String, File> getEnvironmentVariables();

    List<Pair<File, File>> getFileOverwrites();

}
