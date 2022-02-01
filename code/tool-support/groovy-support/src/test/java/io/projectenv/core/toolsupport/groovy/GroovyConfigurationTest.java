package io.projectenv.core.toolsupport.groovy;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class GroovyConfigurationTest {

    @Test
    void testEmptyJson() {
        assertThatExceptionOfType(JsonSyntaxException.class)
                .isThrownBy(() -> parseJson("{}"))
                .withMessageEndingWith("some of required attributes are not set [version]");
    }

    @Test
    void testEmptyJsonWithVersion() {
        assertThat(parseJson("{version:'1.2.3'}")).isNotNull();
    }

    private GroovyConfiguration parseJson(String json) {
        var gson = new GsonBuilder()
                .registerTypeAdapterFactory(new GsonAdaptersGroovyConfiguration())
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        return gson.fromJson(json, GroovyConfiguration.class);
    }

}