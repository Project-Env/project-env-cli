package io.projectenv.core.cli.index;

import com.google.gson.Gson;
import io.projectenv.commons.gson.GsonFactory;
import io.projectenv.core.cli.ProjectEnvException;
import io.projectenv.core.commons.process.ProcessOutput;
import io.projectenv.core.commons.system.CpuArchitecture;
import io.projectenv.core.commons.system.EnvironmentVariables;
import io.projectenv.core.commons.system.OperatingSystem;
import io.projectenv.core.toolsupport.commons.ToolVersionHelper;
import io.projectenv.core.toolsupport.spi.index.ToolsIndexException;
import io.projectenv.core.toolsupport.spi.index.ToolsIndexManager;
import io.projectenv.core.toolsupport.spi.index.ToolsIndexV2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class DefaultToolsIndexManager implements ToolsIndexManager {

    private static final String DEFAULT_TOOLS_INDEX_URL = "https://raw.githubusercontent.com/Project-Env/project-env-tools/main/index-v2.json";
    private static final String PROJECT_ENV_TOOL_INDEX_ENV = "PROJECT_ENV_TOOL_INDEX_V2";
    private static final String TOOLS_INDEX_FILE = "tools-index-v2.json";

    private static final Gson GSON = GsonFactory.createGsonBuilder().create();

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private final File toolsRoot;

    private ToolsIndexV2 toolsIndexV2;

    public DefaultToolsIndexManager(File toolsRoot) {
        this.toolsRoot = toolsRoot;
    }

    private ToolsIndexV2 loadToolsIndex() throws IOException {
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
            return GSON.fromJson(reader, ToolsIndexV2.class);
        }
    }

    private void downloadToolsIndex(File toolsIndexFile) throws IOException, InterruptedException {
        try (InputStream inputStream = new BufferedInputStream(getToolsIndexInputStream());
             OutputStream outputStream = new FileOutputStream(toolsIndexFile)) {
            IOUtils.copy(inputStream, outputStream);
        }
    }

    private InputStream getToolsIndexInputStream() throws IOException, InterruptedException {
        URI toolIndexUri = resolveToolIndexUri();
        if (StringUtils.equals("file", toolIndexUri.getScheme())) {
            return getToolsIndexInputStreamFromFileUri(toolIndexUri);
        } else {
            return getToolsIndexInputStreamFromHttpUri(toolIndexUri);
        }
    }

    private URI resolveToolIndexUri() {
        String toolIndexUri = EnvironmentVariables.get(PROJECT_ENV_TOOL_INDEX_ENV);
        if (toolIndexUri == null) {
            toolIndexUri = DEFAULT_TOOLS_INDEX_URL;
        }

        return URI.create(toolIndexUri);
    }

    private InputStream getToolsIndexInputStreamFromFileUri(URI toolIndexUri) throws IOException {
        return new FileInputStream(new File(toolIndexUri));
    }

    private InputStream getToolsIndexInputStreamFromHttpUri(URI toolIndexUri) throws IOException, InterruptedException {
        var httpRequest = HttpRequest.newBuilder().uri(toolIndexUri).GET().build();
        HttpResponse<InputStream> response = HTTP_CLIENT.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
        if (response.statusCode() != 200) {
            throw new ProjectEnvException("Received a non expected status code " + response.statusCode() + " while downloading the tool index");
        }

        return response.body();
    }

    @Override
    public String resolveMavenDistributionUrl(String version) {
        return Optional.ofNullable(getToolsIndex().getMavenVersions().get(ToolVersionHelper.getVersionWithoutPrefix(version)))
                .orElseThrow(() -> new ToolsIndexException("failed to resolve Maven " + version + " from tool index"));
    }

    @Override
    public Set<String> getMavenVersions() {
        return getToolsIndex().getMavenVersions().keySet();
    }

    @Override
    public String resolveGradleDistributionUrl(String version) {
        return Optional.ofNullable(getToolsIndex().getGradleVersions().get(ToolVersionHelper.getVersionWithoutPrefix(version)))
                .orElseThrow(() -> new ToolsIndexException("failed to resolve Gradle " + version + " from tool index"));
    }

    @Override
    public Set<String> getGradleVersions() {
        return getToolsIndex().getGradleVersions().keySet();
    }

    @Override
    public String resolveNodeJsDistributionUrl(String version) {
        return Optional.ofNullable(getToolsIndex().getNodeVersions().get(ToolVersionHelper.getVersionWithoutPrefix(version)))
                .map(versionEntry -> versionEntry.get(OperatingSystem.getCurrentOperatingSystem()))
                .map(this::resolveDownloadUrlForCpuArchitecture)
                .orElseThrow(() -> new ToolsIndexException("failed to resolve NodeJS " + version + " from tool index"));
    }

    @Override
    public Set<String> getNodeJsVersions() {
        return getToolsIndex().getNodeVersions().keySet();
    }

    @Override
    public String resolveJdkDistributionUrl(String jdkDistribution, String version) {
        return resolveJdkDistributionId(jdkDistribution)
                .map(jdkDistributionId -> getToolsIndex().getJdkVersions().get(jdkDistributionId))
                .map(jdkDistributionEntry -> jdkDistributionEntry.get(ToolVersionHelper.getVersionWithoutPrefix(version)))
                .map(versionEntry -> versionEntry.get(OperatingSystem.getCurrentOperatingSystem()))
                .map(this::resolveDownloadUrlForCpuArchitecture)
                .orElseThrow(() -> new ToolsIndexException("failed to resolve " + jdkDistribution + " " + version + " from tool index"));
    }

    @Override
    public Set<String> getJdkDistributionVersions(String jdkDistribution) {
        return resolveJdkDistributionId(jdkDistribution)
                .map(jdkDistributionId -> getToolsIndex().getJdkVersions().get(jdkDistributionId))
                .map(Map::keySet)
                .orElse(Collections.emptySet());
    }

    private String resolveDownloadUrlForCpuArchitecture(Map<CpuArchitecture, String> downloadUrls) {
        String downloadUrl = downloadUrls.get(CpuArchitecture.getCurrentCpuArchitecture());

        // In case there is no download URL for Apple Silicon, we use the amd64 as fallback
        if (downloadUrl == null &&
                CpuArchitecture.getCurrentCpuArchitecture() == CpuArchitecture.AARCH64 &&
                OperatingSystem.getCurrentOperatingSystem() == OperatingSystem.MACOS) {
            downloadUrl = downloadUrls.get(CpuArchitecture.AMD64);
        }

        return downloadUrl;
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

    private ToolsIndexV2 getToolsIndex() {
        try {
            if (toolsIndexV2 == null) {
                toolsIndexV2 = loadToolsIndex();
            }

            return toolsIndexV2;
        } catch (IOException e) {
            throw new ProjectEnvException("failed to load tool index", e);
        }
    }

}
