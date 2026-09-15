package com.ailab.admin.assets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;

class AssetStorageServiceTest {

    @TempDir
    Path tempDir;

    private AssetStorageService storageService;

    @BeforeEach
    void setUp() {
        storageService = new AssetStorageService(tempDir.resolve("assets").toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        if (Files.exists(tempDir)) {
            Files.walk(tempDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                        } catch (IOException ignored) {}
                    });
        }
    }

    @Test
    void testStoreAndLoad() throws IOException {
        String fileId = "test-asset-123";
        byte[] content = "Hello World Asset Data".getBytes(StandardCharsets.UTF_8);
        String contentType = "text/plain";

        AssetStorageService.StoredAssetMeta meta = storageService.store(fileId, content, contentType);

        assertThat(meta).isNotNull();
        assertThat(meta.fileId()).isEqualTo(fileId);
        assertThat(meta.sizeBytes()).isEqualTo(content.length);
        assertThat(meta.contentType()).isEqualTo(contentType);
        assertThat(meta.sha256Hex()).isNotBlank();

        byte[] loaded = storageService.load(fileId);
        assertThat(loaded).isNotNull();
        assertThat(loaded).isEqualTo(content);
    }

    @Test
    void testGetMetaAndExists() throws IOException {
        String fileId = "meta-test-456";
        byte[] content = "PNG_SAMPLE_DATA".getBytes(StandardCharsets.UTF_8);

        assertThat(storageService.exists(fileId)).isFalse();
        assertThat(storageService.getMeta(fileId)).isNull();

        storageService.store(fileId, content, "image/png");

        assertThat(storageService.exists(fileId)).isTrue();
        AssetStorageService.StoredAssetMeta meta = storageService.getMeta(fileId);
        assertThat(meta).isNotNull();
        assertThat(meta.fileId()).isEqualTo(fileId);
        assertThat(meta.sizeBytes()).isEqualTo(content.length);
        assertThat(meta.contentType()).isEqualTo("image/png");
    }

    @Test
    void testPathTraversalSanitization() throws IOException {
        String maliciousFileId = "../../secret/malicious.txt";
        byte[] content = "malicious payload".getBytes(StandardCharsets.UTF_8);

        AssetStorageService.StoredAssetMeta meta = storageService.store(maliciousFileId, content, "text/plain");

        assertThat(meta.fileId()).doesNotContain("..");
        assertThat(storageService.exists(maliciousFileId)).isTrue();
        byte[] loaded = storageService.load(maliciousFileId);
        assertThat(loaded).isEqualTo(content);
    }

    @Test
    void testComputeSha256() {
        byte[] data = "test-string".getBytes(StandardCharsets.UTF_8);
        String sha = AssetStorageService.computeSha256(data);
        assertThat(sha).isNotNull().hasSize(64);
    }

    @Test
    void testDefaultConstructorInitializesSafely() {
        AssetStorageService defaultService = new AssetStorageService();
        assertThat(defaultService.getStorageDir()).isNotNull();
    }

    @Test
    void testResilientFallbackWhenPathCannotBeCreated() {
        // Provide an illegal / invalid path that causes Files.createDirectories to fail
        String invalidPath = System.getProperty("os.name").toLowerCase().contains("win")
                ? "Z:\\non-existent-drive-letter-12345:\\invalid"
                : "/dev/null/impossible/path";

        AssetStorageService resilientService = new AssetStorageService(invalidPath);
        assertThat(resilientService.getStorageDir()).isNotNull();
        assertThat(Files.exists(resilientService.getStorageDir())).isTrue();
        assertThat(resilientService.getStorageDir().toString()).contains("ailab-storage");
    }

    @Test
    void testWorkspacePreviewServiceFallback() {
        String invalidPath = System.getProperty("os.name").toLowerCase().contains("win")
                ? "Z:\\non-existent-drive-letter-12345:\\invalid"
                : "/dev/null/impossible/path";

        com.ailab.workspace.service.WorkspacePreviewService previewService =
                new com.ailab.workspace.service.WorkspacePreviewService(null, null, null, invalidPath);
        assertThat(previewService.getAssetRoot()).isNotNull();
        assertThat(Files.exists(previewService.getAssetRoot())).isTrue();
        assertThat(previewService.getAssetRoot().toString()).contains("ailab-storage");
    }
}

