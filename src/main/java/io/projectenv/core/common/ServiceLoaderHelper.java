package io.projectenv.core.common;

import java.util.ServiceLoader;

public class ServiceLoaderHelper {

    public static <Service> ServiceLoader<Service> loadService(Class<Service> serviceClass) {
        return ServiceLoader.load(serviceClass, serviceClass.getClassLoader());
    }

}
