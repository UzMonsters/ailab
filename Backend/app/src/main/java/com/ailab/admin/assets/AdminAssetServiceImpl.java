package com.ailab.admin.assets;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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

    @Override
    public Map<String, Object> generateUploadUrls(List<Map<String, Object>> files) {
        if (files == null || files.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_ERROR: Files list cannot be empty");
        }

        List<Map<String, Object>> uploads = new ArrayList<>();

        for (Map<String, Object> file : files) {
            String filename = file.get("filename") != null ? String.valueOf(file.get("filename")) : "unnamed_file";
            String contentType = file.get("contentType") != null ? String.valueOf(file.get("contentType")).toLowerCase() : "application/octet-stream";
            long sizeBytes = file.get("sizeBytes") != null ? ((Number) file.get("sizeBytes")).longValue() : 0L;

            if (!ALLOWED_MIME_TYPES.contains(contentType)) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_ERROR: MIME type not allowed: " + contentType);
            }

            if (sizeBytes > MAX_IMAGE_BYTES) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_ERROR: File size exceeds maximum limit of " + MAX_IMAGE_BYTES + " bytes");
            }

            String fileId = "ast_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
            Instant expiresAt = Instant.now().plus(1, ChronoUnit.HOURS);

            uploads.add(Map.of(
                    "fileId", fileId,
                    "filename", filename,
                    "contentType", contentType,
                    "uploadUrl", "https://storage.jasscience.dev/uploads/" + fileId + "/" + filename,
                    "downloadUrl", "https://storage.jasscience.dev/assets/" + fileId + "/" + filename,
                    "expiresAt", expiresAt
            ));
        }

        return Map.of("uploads", uploads);
    }
}
