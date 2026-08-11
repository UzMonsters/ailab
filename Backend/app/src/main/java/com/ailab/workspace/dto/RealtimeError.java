package com.ailab.workspace.dto;

public record RealtimeError(
        String code,
        String message,
        String clientEventId,
        Long expectedVersion,
        Long actualVersion
) {}
