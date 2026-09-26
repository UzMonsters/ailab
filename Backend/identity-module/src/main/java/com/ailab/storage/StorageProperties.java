package com.ailab.storage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "app.storage")
public record StorageProperties(
        String provider,
        URI endpoint,
        @NotBlank String region,
        String bucket,
        String accessKey,
        String secretKey,
        boolean pathStyle,
        boolean createBucket,
        @NotNull Duration downloadUrlTtl,
        @NotNull DataSize maxAssetSize,
        @NotNull DataSize maxAvatarSize,
        @NotNull DataSize maxPreviewSize,
        @NotBlank String localDir
) {
    private static final Duration MAX_DOWNLOAD_TTL = Duration.ofHours(1);

    public StorageProperties {
        StorageProvider parsed = StorageProvider.parse(provider);
        if (region == null || region.isBlank()) {
            region = "us-east-1";
        }
        if (bucket == null || bucket.isBlank()) {
            bucket = "ailab-local";
        }
        if (downloadUrlTtl == null) {
            downloadUrlTtl = Duration.ofMinutes(10);
        }
        if (maxAssetSize == null) {
            maxAssetSize = DataSize.ofMegabytes(10);
        }
        if (maxAvatarSize == null) {
            maxAvatarSize = DataSize.ofMegabytes(2);
        }
        if (maxPreviewSize == null) {
            maxPreviewSize = DataSize.ofMegabytes(10);
        }
        if (localDir == null || localDir.isBlank()) {
            localDir = "./storage/objects";
        }
        if (parsed == StorageProvider.S3 && (bucket == null || bucket.isBlank())) {
            throw new IllegalArgumentException("Storage bucket is required for S3.");
        }
        if (downloadUrlTtl == null || downloadUrlTtl.isNegative() || downloadUrlTtl.isZero()
                || downloadUrlTtl.compareTo(MAX_DOWNLOAD_TTL) > 0) {
            throw new IllegalArgumentException("Storage download URL TTL must be positive and no more than PT1H.");
        }
        if (maxAssetSize == null || maxAssetSize.toBytes() <= 0
                || maxAvatarSize == null || maxAvatarSize.toBytes() <= 0
                || maxPreviewSize == null || maxPreviewSize.toBytes() <= 0) {
            throw new IllegalArgumentException("Storage size limits must be positive.");
        }
        provider = parsed.name().toLowerCase();
    }

    public StorageProvider parsedProvider() {
        return StorageProvider.parse(provider);
    }
}
