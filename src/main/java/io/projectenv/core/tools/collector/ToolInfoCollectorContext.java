package io.projectenv.core.tools.collector;

import org.immutables.value.Value;

import java.io.File;

@Value.Immutable
public interface ToolInfoCollectorContext {

    File getProjectRoot();

    File getToolRoot();

}
