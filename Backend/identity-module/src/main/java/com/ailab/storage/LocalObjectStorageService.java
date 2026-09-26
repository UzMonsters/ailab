package com.ailab.storage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Properties;

@Service
@ConditionalOnProperty(prefix = "app.storage", name = "provider", havingValue = "local", matchIfMissing = true)
public class LocalObjectStorageService implements ObjectStorageService {
    private final Path root;

    @org.springframework.beans.factory.annotation.Autowired
    public LocalObjectStorageService(StorageProperties properties) {
        this(properties.localDir());
    }

    public LocalObjectStorageService(String localDir) {
        try {
            this.root = Path.of(localDir).toAbsolutePath().normalize();
            Files.createDirectories(root);
        } catch (IOException exception) {
            throw new StorageException("Local object storage could not be initialized.", exception);
        }
    }

    @Override
    public StoredObject upload(StorageUpload upload) {
        Path target = resolve(upload.storageKey());
        try {
            Files.createDirectories(target.getParent());
            byte[] bytes = upload.inputStream().readAllBytes();
            Files.write(target, bytes);
            Properties meta = new Properties();
            meta.setProperty("contentType", upload.contentType() != null ? upload.contentType() : "application/octet-stream");
            meta.setProperty("sizeBytes", String.valueOf(bytes.length));
            try (var out = Files.newOutputStream(metaPath(target))) {
                meta.store(out, "AI Laboratory local object metadata");
            }
            return new StoredObject(upload.storageKey(), meta.getProperty("contentType"), bytes.length);
        } catch (IOException exception) {
            throw new StorageException("Local object upload failed.", exception);
        }
    }

    @Override
    public StoredObjectDownload download(String storageKey) {
        Path target = resolve(storageKey);
        if (!Files.exists(target)) {
            throw new StorageException("Object not found.");
        }
        try {
            byte[] bytes = Files.readAllBytes(target);
            Properties meta = metadata(target, bytes.length);
            return new StoredObjectDownload(
                    meta.getProperty("contentType", "application/octet-stream"),
                    Long.parseLong(meta.getProperty("sizeBytes", String.valueOf(bytes.length))),
                    new ByteArrayInputStream(bytes));
        } catch (IOException exception) {
            throw new StorageException("Local object download failed.", exception);
        }
    }

    @Override
    public boolean exists(String storageKey) {
        return Files.exists(resolve(storageKey));
    }

    @Override
    public void delete(String storageKey) {
        Path target = resolve(storageKey);
        try {
            Files.deleteIfExists(target);
            Files.deleteIfExists(metaPath(target));
        } catch (IOException exception) {
            throw new StorageException("Local object deletion failed.", exception);
        }
    }

    @Override
    public URI createPresignedDownloadUrl(String storageKey, String downloadFileName, Duration ttl) {
        resolve(storageKey);
        return URI.create("local://" + storageKey);
    }

    private Path resolve(String storageKey) {
        if (storageKey == null || storageKey.isBlank() || storageKey.startsWith("/") || storageKey.startsWith("\\")
                || storageKey.contains("..")) {
            throw new StorageException("Invalid storage key.");
        }
        Path target = root.resolve(storageKey).normalize();
        if (!target.startsWith(root)) {
            throw new StorageException("Invalid storage key.");
        }
        return target;
    }

    private Properties metadata(Path target, long fallbackSize) throws IOException {
        Properties meta = new Properties();
        Path metaPath = metaPath(target);
        if (Files.exists(metaPath)) {
            try (var in = Files.newInputStream(metaPath)) {
                meta.load(in);
            }
        }
        meta.putIfAbsent("contentType", Files.probeContentType(target) != null
                ? Files.probeContentType(target)
                : "application/octet-stream");
        meta.putIfAbsent("sizeBytes", String.valueOf(fallbackSize));
        return meta;
    }

    private Path metaPath(Path target) {
        return target.resolveSibling(target.getFileName() + ".meta");
    }
}
