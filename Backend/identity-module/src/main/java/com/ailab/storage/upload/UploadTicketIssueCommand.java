package com.ailab.storage.upload;

import java.time.Duration;

public record UploadTicketIssueCommand(
        String assetId,
        String actorId,
        UploadScope scope,
        String storageKey,
        String allowedMime,
        long maxSizeBytes,
        String expectedChecksum,
        Duration ttl,
        String workspaceId,
        String previewId,
        String variant
) {
}
