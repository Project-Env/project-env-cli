package ch.projectenv.core.toolinfo.collector;

import org.immutables.value.Value;

import java.io.File;

@Value.Immutable
public interface ToolInfoCollectorContext {

    File getProjectRoot();

    File getToolBinariesRoot();

}
