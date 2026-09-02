package com.ailab.workspace.dto;

import java.time.Instant;
import java.util.List;

public record PreviewUploadUrlsResponse(
        String previewId,
        Long sourceStateVersion,
        List<UploadTarget> uploads
) {
    public record UploadTarget(
            String theme,
            String assetId,
            String uploadUrl,
            Instant expiresAt
    ) {}
}
