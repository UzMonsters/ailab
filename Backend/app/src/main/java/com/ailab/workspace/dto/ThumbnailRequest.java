package com.ailab.workspace.dto;

public record ThumbnailRequest(
        String svg,
        Integer width,
        Integer height,
        String imageData
) {}
