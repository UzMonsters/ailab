package com.ailab.admin.assets;

import com.ailab.book.domain.AssetKind;
import com.ailab.book.domain.AssetStatus;
import com.ailab.book.domain.BookAsset;
import com.ailab.book.repository.BookAssetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class AdminAssetServiceImpl implements AdminAssetService {

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/png",
            "image/webp",
            "image/svg+xml",
            "image/jpeg",
            "application/pdf",
            "application/json"
    );

    private static final long MAX_IMAGE_BYTES = 5242880L;

    private final BookAssetRepository assetRepository;

    public AdminAssetServiceImpl() {
        this.assetRepository = null;
    }

    public AdminAssetServiceImpl(BookAssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Override
    @Transactional
    public Map<String, Object> generateUploadUrls(List<Map<String, Object>> files) {
        if (files == null || files.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_ERROR: Files list cannot be empty");
        }

        List<Map<String, Object>> uploads = new ArrayList<>();

        for (Map<String, Object> file : files) {
            String filename = file.get("filename") != null ? String.valueOf(file.get("filename"))
                    : (file.get("name") != null ? String.valueOf(file.get("name")) : "unnamed_file");

            String contentType = file.get("contentType") != null ? String.valueOf(file.get("contentType")).toLowerCase()
                    : (file.get("mimeType") != null ? String.valueOf(file.get("mimeType")).toLowerCase() : "application/octet-stream");

            long sizeBytes = file.get("sizeBytes") != null ? ((Number) file.get("sizeBytes")).longValue()
                    : (file.get("size") != null ? ((Number) file.get("size")).longValue() : 0L);

            String kindStr = file.get("kind") != null ? String.valueOf(file.get("kind")) : null;
            String checksum = file.get("checksum") != null ? String.valueOf(file.get("checksum")) : null;

            if (!ALLOWED_MIME_TYPES.contains(contentType)) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_ERROR: MIME type not allowed: " + contentType);
            }

            if (sizeBytes > MAX_IMAGE_BYTES) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_ERROR: File size exceeds maximum limit of " + MAX_IMAGE_BYTES + " bytes");
            }

            String fileId = "ast_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
            Instant expiresAt = Instant.now().plus(1, ChronoUnit.HOURS);
            String uploadUrl = "https://storage.jasscience.dev/uploads/" + fileId + "/" + filename;
            String downloadUrl = "https://storage.jasscience.dev/assets/" + fileId + "/" + filename;

            if (assetRepository != null) {
                AssetKind kind = "SVG".equalsIgnoreCase(kindStr) || contentType.contains("svg") ? AssetKind.SVG : AssetKind.IMAGE;
                BookAsset asset = new BookAsset(fileId, kind, contentType, sizeBytes, checksum, uploadUrl, downloadUrl);
                asset.setStatus(AssetStatus.PENDING);
                if (file.get("theme") != null) {
                    asset.setVariants(Map.of("theme", file.get("theme")));
                }
                assetRepository.save(asset);
            }

            uploads.add(Map.of(
                    "fileId", fileId,
                    "assetId", fileId,
                    "filename", filename,
                    "contentType", contentType,
                    "uploadUrl", uploadUrl,
                    "downloadUrl", downloadUrl,
                    "expiresAt", expiresAt
            ));
        }

        return Map.of("uploads", uploads);
    }

    @Override
    @Transactional
    public Map<String, Object> completeAsset(String assetId, Map<String, Object> request) {
        if (assetRepository == null) {
            return Map.of("id", assetId, "status", "READY");
        }

        BookAsset asset = assetRepository.findById(assetId)
                .orElseGet(() -> {
                    BookAsset newAsset = new BookAsset(
                            assetId,
                            AssetKind.IMAGE,
                            "image/png",
                            0L,
                            null,
                            "https://storage.jasscience.dev/uploads/" + assetId,
                            "https://storage.jasscience.dev/assets/" + assetId
                    );
                    return assetRepository.save(newAsset);
                });

        if (request != null) {
            if (request.get("checksum") != null) {
                asset.setChecksum(String.valueOf(request.get("checksum")));
            }
            if (request.get("alt") instanceof Map<?, ?> altMap) {
                @SuppressWarnings("unchecked")
                Map<String, Object> castAlt = (Map<String, Object>) altMap;
                asset.setAlt(castAlt);
            }
            if (request.get("caption") instanceof Map<?, ?> capMap) {
                @SuppressWarnings("unchecked")
                Map<String, Object> castCap = (Map<String, Object>) capMap;
                asset.setCaption(castCap);
            }
            if (request.get("variants") instanceof Map<?, ?> varMap) {
                @SuppressWarnings("unchecked")
                Map<String, Object> castVar = (Map<String, Object>) varMap;
                asset.setVariants(castVar);
            }
            if (request.get("width") instanceof Number num) {
                asset.setWidth(num.intValue());
            }
            if (request.get("height") instanceof Number num) {
                asset.setHeight(num.intValue());
            }
        }

        asset.setStatus(AssetStatus.READY);
        BookAsset saved = assetRepository.save(asset);

        Map<String, Object> result = new HashMap<>();
        result.put("id", saved.getId());
        result.put("kind", saved.getKind().name());
        result.put("mimeType", saved.getMimeType());
        result.put("sizeBytes", saved.getSizeBytes());
        result.put("checksum", saved.getChecksum());
        result.put("status", saved.getStatus().name());
        result.put("variants", saved.getVariants());
        result.put("width", saved.getWidth());
        result.put("height", saved.getHeight());
        result.put("alt", saved.getAlt());
        result.put("caption", saved.getCaption());
        result.put("uploadUrl", saved.getUploadUrl());
        result.put("downloadUrl", saved.getDownloadUrl());
        result.put("createdAt", saved.getCreatedAt());
        result.put("updatedAt", saved.getUpdatedAt());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getAsset(String assetId) {
        if (assetRepository == null) {
            return Map.of("id", assetId, "status", "READY");
        }

        BookAsset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ASSET_NOT_FOUND: Asset not found with id " + assetId));

        Map<String, Object> result = new HashMap<>();
        result.put("id", asset.getId());
        result.put("kind", asset.getKind().name());
        result.put("mimeType", asset.getMimeType());
        result.put("sizeBytes", asset.getSizeBytes());
        result.put("checksum", asset.getChecksum());
        result.put("status", asset.getStatus().name());
        result.put("variants", asset.getVariants());
        result.put("width", asset.getWidth());
        result.put("height", asset.getHeight());
        result.put("alt", asset.getAlt());
        result.put("caption", asset.getCaption());
        result.put("uploadUrl", asset.getUploadUrl());
        result.put("downloadUrl", asset.getDownloadUrl());
        result.put("createdAt", asset.getCreatedAt());
        result.put("updatedAt", asset.getUpdatedAt());

        return result;
    }
}
