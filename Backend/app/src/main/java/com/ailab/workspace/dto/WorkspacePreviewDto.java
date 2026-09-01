package com.ailab.workspace.dto;

import java.util.Map;

public record WorkspacePreviewDto(
        String status, // READY, PROCESSING, FALLBACK, ERROR
        Long sourceStateVersion,
        String generatedAt,
        Map<String, PreviewVariantDto> variants,
        Map<String, Object> fallback
) {
    public static WorkspacePreviewDto fallback(String key) {
        return new WorkspacePreviewDto("READY", 1L, null, Map.of(), Map.of("kind", "STATIC", "key", key != null ? key : "chemistry-default-01"));
    }

    public static WorkspacePreviewDto of(Long sourceStateVersion, String darkUrl, String lightUrl, String fallbackKey) {
        Map<String, PreviewVariantDto> variants = Map.of(
                "dark", new PreviewVariantDto(darkUrl != null ? darkUrl : "", 960, 540, "image/webp"),
                "light", new PreviewVariantDto(lightUrl != null ? lightUrl : "", 960, 540, "image/webp")
        );
        return new WorkspacePreviewDto("READY", sourceStateVersion, null, variants, Map.of("kind", "STATIC", "key", fallbackKey != null ? fallbackKey : "chemistry-default-01"));
    }

    public record PreviewVariantDto(
            String url,
            Integer width,
            Integer height,
            String mimeType
    ) {}
}
