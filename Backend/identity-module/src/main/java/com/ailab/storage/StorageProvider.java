package com.ailab.storage;

public enum StorageProvider {
    LOCAL,
    S3;

    static StorageProvider parse(String value) {
        if (value == null || value.isBlank()) {
            return LOCAL;
        }
        return switch (value.trim().toLowerCase()) {
            case "local" -> LOCAL;
            case "s3" -> S3;
            default -> throw new IllegalArgumentException("Unsupported storage provider: " + value);
        };
    }
}
