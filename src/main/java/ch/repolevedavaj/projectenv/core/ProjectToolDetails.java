package ch.repolevedavaj.projectenv.core;

import org.immutables.value.Value;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Value.Immutable
public interface ProjectToolDetails {

    ProjectToolType getType();

    File getLocation();

    Optional<File> getPrimaryExecutable();

    List<File> getPathElements();

    Map<String, File> getExports();

}
