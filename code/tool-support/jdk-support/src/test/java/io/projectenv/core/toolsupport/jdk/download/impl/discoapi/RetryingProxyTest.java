package io.projectenv.core.toolsupport.jdk.download.impl.discoapi;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.mockito.Mockito.*;

class RetryingProxyTest {

    @Test
    void testMaxRetries() throws Exception {
        var retryingProxyTestTargetMock = mock(RetryingProxyTestTarget.class);
        when(retryingProxyTestTargetMock.doSomething()).thenThrow(new RuntimeException());

        var proxy = RetryingProxy.wrap(retryingProxyTestTargetMock, 3, Duration.ofMillis(100));
        Assertions.assertThatExceptionOfType(RuntimeException.class).isThrownBy(proxy::doSomething);

        verify(retryingProxyTestTargetMock, times(3)).doSomething();
    }

    private interface RetryingProxyTestTarget {

        String doSomething();

    }

}