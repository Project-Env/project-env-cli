package io.projectenv.core.tools.repository;

import io.projectenv.core.tools.repository.impl.DefaultToolsRepository;

import java.io.File;

public final class ToolsRepositoryFactory {

    private ToolsRepositoryFactory() {
        // noop
    }

    public static ToolsRepository createToolRepository(File repositoryRoot) {
        return new DefaultToolsRepository(repositoryRoot);
    }

}
