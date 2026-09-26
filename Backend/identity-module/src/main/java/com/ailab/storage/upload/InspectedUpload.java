package com.ailab.storage.upload;

public record InspectedUpload(
        byte[] bytes,
        String declaredMime,
        String detectedMime,
        String sha256
) {
}
