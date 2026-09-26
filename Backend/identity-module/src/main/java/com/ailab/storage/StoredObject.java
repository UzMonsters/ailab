package com.ailab.storage;

public record StoredObject(
        String storageKey,
        String contentType,
        long sizeBytes
) {
}
