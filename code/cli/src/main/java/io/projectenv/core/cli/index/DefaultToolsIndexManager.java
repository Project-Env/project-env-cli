package io.projectenv.core.cli.index;

import com.google.gson.Gson;
import io.projectenv.commons.gson.GsonFactory;
import io.projectenv.core.cli.ProjectEnvException;
import io.projectenv.core.commons.process.ProcessOutput;
import io.projectenv.core.commons.system.OperatingSystem;
import io.projectenv.core.toolsupport.spi.index.ToolsIndex;
import io.projectenv.core.toolsupport.spi.index.ToolsIndexException;
import io.projectenv.core.toolsupport.spi.index.ToolsIndexManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class DefaultToolsIndexManager implements ToolsIndexManager {

    private static final String TOOLS_INDEX_URL = "https://raw.githubusercontent.com/Project-Env/project-env-tools/main/index.json";
    private static final String TOOLS_INDEX_FILE = "tools-index.json";

    private static final Gson GSON = GsonFactory.createGsonBuilder().create();

    private final File toolsRoot;

    private ToolsIndex toolsIndex;

    public DefaultToolsIndexManager(File toolsRoot) {
        this.toolsRoot = toolsRoot;
    }

    private ToolsIndex loadToolsIndex() throws IOException {
        var toolsIndexFile = new File(toolsRoot, TOOLS_INDEX_FILE);
        FileUtils.forceMkdirParent(toolsIndexFile);

        try {
            downloadToolsIndex(toolsIndexFile);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ProjectEnvException("interrupted while downloading tools index", e);
        } catch (Exception e) {
            ProcessOutput.writeDebugMessage("failed to update tools index: {0}", e.getMessage());
            ProcessOutput.writeDebugMessage(e);
        }

        if (!toolsIndexFile.exists()) {
            throw new ProjectEnvException("could not load tools index as the download failed and no local copy is present");
        }

        try (Reader reader = new FileReader(toolsIndexFile, StandardCharsets.UTF_8)) {
            return GSON.fromJson(reader, ToolsIndex.class);
        }
    }

    private void downloadToolsIndex(File toolsIndexFile) throws IOException, InterruptedException {
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(TOOLS_INDEX_URL))
                .GET()
                .build();

        var httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        HttpResponse<InputStream> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
        if (response.statusCode() != 200) {
            throw new ProjectEnvException("Received a non expected status code " + response.statusCode() + " while downloading the tool index");
        }

        try (InputStream inputStream = new BufferedInputStream(response.body());
             OutputStream outputStream = new FileOutputStream(toolsIndexFile)) {
            IOUtils.copy(inputStream, outputStream);
        }
    }

    @Override
    public String resolveMavenDistributionUrl(String version) {
        return Optional.ofNullable(getToolsIndex().getMavenVersions().get(version))
                .orElseThrow(() -> new ToolsIndexException("failed to resolve Maven " + version + " from tool index"));
    }

    @Override
    public String resolveGradleDistributionUrl(String version) {
        return Optional.ofNullable(getToolsIndex().getGradleVersions().get(version))
                .orElseThrow(() -> new ToolsIndexException("failed to resolve Gradle " + version + " from tool index"));
    }

    @Override
    public String resolveNodeJsDistributionUrl(String version) {
        return Optional.ofNullable(getToolsIndex().getNodeVersions().get(version))
                .map(versionEntry -> versionEntry.get(OperatingSystem.getCurrentOperatingSystem()))
                .orElseThrow(() -> new ToolsIndexException("failed to resolve NodeJS " + version + " from tool index"));
    }

    @Override
    public String resolveJdkDistributionUrl(String jdkDistribution, String version) {
        return resolveJdkDistributionId(jdkDistribution)
                .map(jdkDistributionId -> getToolsIndex().getJdkVersions().get(jdkDistributionId))
                .map(jdkDistributionEntry -> jdkDistributionEntry.get(version))
                .map(versionEntry -> versionEntry.get(OperatingSystem.getCurrentOperatingSystem()))
                .orElseThrow(() -> new ToolsIndexException("failed to resolve " + jdkDistribution + " " + version + " from tool index"));
    }

    private Optional<String> resolveJdkDistributionId(String jdkDistribution) {
        var jdkDistributionSynonyms = getToolsIndex().getJdkDistributionSynonyms();
        if (jdkDistributionSynonyms.containsKey(jdkDistribution)) {
            return Optional.of(jdkDistribution);
        }

        for (Map.Entry<String, Set<String>> synonyms : jdkDistributionSynonyms.entrySet()) {
            if (synonyms.getValue().contains(jdkDistribution)) {
                return Optional.of(synonyms.getKey());
            }
        }

        return Optional.empty();
    }

    private ToolsIndex getToolsIndex() {
        try {
            if (toolsIndex == null) {
                toolsIndex = loadToolsIndex();
            }

            return toolsIndex;
        } catch (IOException e) {
            throw new ProjectEnvException("failed to load tool index", e);
        }
    }

}
