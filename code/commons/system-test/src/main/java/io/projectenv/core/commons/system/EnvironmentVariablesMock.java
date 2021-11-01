package io.projectenv.core.commons.system;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;

public class EnvironmentVariablesMock implements AutoCloseable {

    private final Map<String, String> envOverlay;
    private final MockedStatic<EnvironmentVariables> mock;

    private EnvironmentVariablesMock(Map<String, String> envOverlay) {
        this.envOverlay = Map.copyOf(envOverlay);
        this.mock = Mockito.mockStatic(EnvironmentVariables.class);

        setupEnvOverlay();
    }

    private void setupEnvOverlay() {
        mock.when(() -> EnvironmentVariables.get(anyString())).thenAnswer(invocation -> {
            String key = invocation.getArgument(0);
            if (envOverlay.containsKey(key)) {
                return envOverlay.get(key);
            }

            return invocation.callRealMethod();
        });
    }

    @Override
    public void close() throws Exception {
        mock.close();
    }

    public static EnvironmentVariablesMock withEnvOverlay(Map<String, String> overlay) {
        return new EnvironmentVariablesMock(overlay);
    }

}
