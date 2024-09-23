package io.projectenv.core.commons.process;

import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public interface ProcessResult {

    int getExitCode();

    Optional<String> getStdOutput();

    Optional<String> getErrOutput();

}
