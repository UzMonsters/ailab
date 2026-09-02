package com.ailab.workspace.dto;

import java.time.Instant;
import java.util.List;

public record PreviewUploadUrlsRequest(
        Long sourceStateVersion,
        List<VariantRequest> variants
) {
    public record VariantRequest(
            String theme, // DARK, LIGHT
            String mimeType, // image/webp, image/png
            Integer width,
            Integer height,
            String checksum
    ) {}
}
