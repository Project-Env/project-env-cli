package io.projectenv.core.commons.system;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;

public class TestEnvironmentVariables {

    private TestEnvironmentVariables() {
        // noop
    }

    public static AutoCloseable overlayEnv(Map<String, String> overlay) {
        Map<String, String> originalEnv = EnvironmentVariables.get();

        MockedStatic<EnvironmentVariables> mock = Mockito.mockStatic(EnvironmentVariables.class);

        Map<String, String> overlaidEnv = new HashMap<>();
        overlaidEnv.putAll(originalEnv);
        overlaidEnv.putAll(overlay);
        mock.when(EnvironmentVariables::get).thenReturn(overlaidEnv);
        mock.when(() -> EnvironmentVariables.get(anyString())).thenCallRealMethod();

        return mock;
    }

}
