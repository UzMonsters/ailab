package com.ailab.storage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LocalObjectStorageServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void storesDownloadsMetadataChecksExistenceAndDeletesNestedObject() throws Exception {
        LocalObjectStorageService storage = new LocalObjectStorageService(tempDir.toString());

        StoredObject stored = storage.upload(new StorageUpload(
                "assets/asset_123",
                "image/webp",
                5,
                new ByteArrayInputStream("hello".getBytes(StandardCharsets.UTF_8))));

        assertThat(stored.storageKey()).isEqualTo("assets/asset_123");
        assertThat(stored.contentType()).isEqualTo("image/webp");
        assertThat(stored.sizeBytes()).isEqualTo(5);
        assertThat(storage.exists("assets/asset_123")).isTrue();

        try (StoredObjectDownload download = storage.download("assets/asset_123")) {
            assertThat(download.contentType()).isEqualTo("image/webp");
            assertThat(download.sizeBytes()).isEqualTo(5);
            assertThat(download.inputStream().readAllBytes()).isEqualTo("hello".getBytes(StandardCharsets.UTF_8));
        }

        assertThat(storage.createPresignedDownloadUrl("assets/asset_123", "asset.webp", Duration.ofMinutes(5)))
                .hasToString("local://assets/asset_123");

        storage.delete("assets/asset_123");
        assertThat(storage.exists("assets/asset_123")).isFalse();
    }

    @Test
    void rejectsTraversalAndAbsoluteStorageKeys() {
        LocalObjectStorageService storage = new LocalObjectStorageService(tempDir.toString());

        assertThatThrownBy(() -> storage.upload(new StorageUpload(
                "../secret.txt",
                "text/plain",
                1,
                new ByteArrayInputStream(new byte[]{1}))))
                .isInstanceOf(StorageException.class)
                .hasMessageContaining("Invalid storage key");

        assertThatThrownBy(() -> storage.exists("/absolute/path"))
                .isInstanceOf(StorageException.class)
                .hasMessageContaining("Invalid storage key");
    }
}
