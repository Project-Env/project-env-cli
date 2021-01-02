package ch.repolevedavaj.projectenv.core;

import org.immutables.value.Value;

import java.io.File;
import java.util.List;
import java.util.Map;

@Value.Immutable
public interface ProjectToolDetails {

    ProjectToolType getType();

    File getLocation();

    List<File> getPathElements();

    Map<String, File> getExports();

}
