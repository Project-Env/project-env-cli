package io.projectenv.core.common;

import java.util.ServiceLoader;

public final class ServiceLoaderHelper {

    private ServiceLoaderHelper() {
        // noop
    }

    public static <T> ServiceLoader<T> loadService(Class<T> serviceClass) {
        return ServiceLoader.load(serviceClass, serviceClass.getClassLoader());
    }

}
