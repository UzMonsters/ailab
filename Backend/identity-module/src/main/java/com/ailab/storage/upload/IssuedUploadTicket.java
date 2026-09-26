package com.ailab.storage.upload;

import java.time.Instant;

public record IssuedUploadTicket(
        String assetId,
        String token,
        String storageKey,
        Instant expiresAt
) {
}
