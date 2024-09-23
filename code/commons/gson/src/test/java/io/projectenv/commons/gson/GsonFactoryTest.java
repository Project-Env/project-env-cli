package io.projectenv.commons.gson;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class GsonFactoryTest {

    @Test
    void testFileConversion() {
        Gson gson = GsonFactory.createGson();

        File nonCanonicalFile = new File("folder/../file.txt");
        File canonicalFile = new File("file.txt");

        assertThat(gson.toJson(nonCanonicalFile))
                .isEqualTo(getWrappedAbsolutePath(canonicalFile));

        assertThat(gson.fromJson(getWrappedAbsolutePath(canonicalFile), File.class).getAbsolutePath())
                .isEqualTo(canonicalFile.getAbsolutePath());
    }

    @Test
    void testNullFile() {
        Gson gson = GsonFactory.createGson();

        assertThat(gson.toJson(new FileWrapper(null))).isEqualTo("{}");
    }

    private String getWrappedAbsolutePath(File value) {
        return "\"" + value.getAbsolutePath().replace("\\", "\\\\") + "\"";
    }

    private static class FileWrapper {

        public final File file;

        private FileWrapper(File file) {
            this.file = file;
        }
    }

}