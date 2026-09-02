package com.ailab.workspace.dto;

import java.util.List;

public record CompletePreviewRequest(
        Long sourceStateVersion,
        List<AssetResult> assets,
        String fallbackKey
) {
    public record AssetResult(
            String theme,
            String assetId,
            String url,
            String checksum
    ) {}
}
