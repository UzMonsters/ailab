package com.ailab.admin.assets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AssetStorageService {

    private static final Logger log = LoggerFactory.getLogger(AssetStorageService.class);

    private final Path storageDir;
    private final Map<String, StoredAssetMeta> metaCache = new ConcurrentHashMap<>();

    public record StoredAssetMeta(
            String fileId,
            long sizeBytes,
            String contentType,
            String sha256Hex
    ) {}

    public AssetStorageService() {
        this("./storage/assets");
    }

    @org.springframework.beans.factory.annotation.Autowired
    public AssetStorageService(@org.springframework.beans.factory.annotation.Value("${app.storage.dir:./storage/assets}") String storagePath) {
        Path initializedPath;
        try {
            Path resolved = Paths.get(storagePath != null && !storagePath.isBlank() ? storagePath : "./storage/assets");
            Files.createDirectories(resolved);
            initializedPath = resolved;
            log.info("Initialized asset storage directory at: {}", initializedPath.toAbsolutePath());
        } catch (Exception e) {
            log.warn("Failed to create configured asset storage directory for path '{}': {}. Attempting fallback to system temp directory.",
                    storagePath, e.getMessage());
            Path fallback = Paths.get(System.getProperty("java.io.tmpdir"), "ailab-storage", "assets");
            try {
                Files.createDirectories(fallback);
                initializedPath = fallback;
                log.info("Asset storage gracefully initialized with fallback directory at: {}", initializedPath.toAbsolutePath());
            } catch (Exception fallbackEx) {
                log.error("Failed to initialize both primary storage '{}' and fallback storage '{}'", storagePath, fallback, fallbackEx);
                throw new RuntimeException("Failed to initialize asset storage directory at " + storagePath + " and fallback " + fallback, e);
            }
        }
        this.storageDir = initializedPath;
    }

    public Path getStorageDir() {
        return storageDir;
    }



    public StoredAssetMeta store(String fileId, byte[] data, String contentType) throws IOException {
        String safeFileId = sanitize(fileId);
        Path target = storageDir.resolve(safeFileId);
        Files.write(target, data);

        String sha256 = computeSha256(data);
        String ct = (contentType != null && !contentType.isBlank()) ? contentType : "application/octet-stream";
        StoredAssetMeta meta = new StoredAssetMeta(safeFileId, data.length, ct, sha256);
        metaCache.put(safeFileId, meta);
        return meta;
    }

    public byte[] load(String fileId) throws IOException {
        String safeFileId = sanitize(fileId);
        Path target = storageDir.resolve(safeFileId);
        if (!Files.exists(target)) {
            return null;
        }
        return Files.readAllBytes(target);
    }

    public StoredAssetMeta getMeta(String fileId) {
        String safeFileId = sanitize(fileId);
        StoredAssetMeta cached = metaCache.get(safeFileId);
        if (cached != null) {
            return cached;
        }
        Path target = storageDir.resolve(safeFileId);
        if (!Files.exists(target)) {
            return null;
        }
        try {
            byte[] data = Files.readAllBytes(target);
            String sha256 = computeSha256(data);
            String probeType = Files.probeContentType(target);
            String ct = probeType != null ? probeType : "application/octet-stream";
            StoredAssetMeta meta = new StoredAssetMeta(safeFileId, data.length, ct, sha256);
            metaCache.put(safeFileId, meta);
            return meta;
        } catch (IOException e) {
            return null;
        }
    }

    public boolean exists(String fileId) {
        String safeFileId = sanitize(fileId);
        return Files.exists(storageDir.resolve(safeFileId));
    }

    public static String computeSha256(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            return HexFormat.of().formatHex(hash).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    private String sanitize(String fileId) {
        if (fileId == null) {
            return "unnamed_asset";
        }
        String clean = fileId.replace("..", "_").replaceAll("[^a-zA-Z0-9._-]", "_");
        return clean.isBlank() ? "unnamed_asset" : clean;
    }
}

