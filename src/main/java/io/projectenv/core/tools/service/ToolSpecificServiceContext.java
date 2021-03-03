package io.projectenv.core.tools.service;

import org.immutables.value.Value;

import java.io.File;

@Value.Immutable
public interface ToolSpecificServiceContext {

    File getProjectRoot();

    File getToolRoot();

}
