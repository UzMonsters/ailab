package com.ailab.storage;

import java.io.InputStream;

public record StorageUpload(
        String storageKey,
        String contentType,
        long sizeBytes,
        InputStream inputStream
) {
}
