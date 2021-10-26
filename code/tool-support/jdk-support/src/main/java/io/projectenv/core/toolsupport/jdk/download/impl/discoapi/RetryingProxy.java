package io.projectenv.core.toolsupport.jdk.download.impl.discoapi;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.Duration;

public class RetryingProxy {

    private RetryingProxy() {
        // noop
    }

    @SuppressWarnings("unchecked")
    public static <T> T wrap(T target, int maxTries, Duration retryTimeout) {
        return (T) Proxy.newProxyInstance(
                RetryingProxy.class.getClassLoader(),
                target.getClass().getInterfaces(),
                new RetryingInvocationHandler(target, maxTries, retryTimeout.toMillis()));
    }

    private static class RetryingInvocationHandler implements InvocationHandler {

        private final Object target;
        private final int maxTries;
        private final long retryTimeout;

        private RetryingInvocationHandler(Object target, int maxTries, long retryTimeout) {
            this.target = target;
            this.maxTries = maxTries;
            this.retryTimeout = retryTimeout;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            int tries = 1;

            while (true) {
                try {
                    return method.invoke(target, args);
                } catch (Exception e) {
                    if (tries == maxTries) {
                        throw e;
                    }

                    waitForRetryTimoutFinished();
                    tries++;
                }
            }
        }

        private void waitForRetryTimoutFinished() throws IOException {
            try {
                Thread.sleep(retryTimeout);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

                throw new IOException(e);
            }
        }

    }

}
