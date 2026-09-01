package com.ailab.workspace.dto;

import java.util.Map;

public record WorkspaceConnectionDto(
        String id,
        String fromItemId,
        String fromPortId,
        String toItemId,
        String toPortId,
        String type, // FLUID, THERMAL, SENSOR, GAS, POWER
        String status, // CONNECTED, FLOWING, BLOCKED
        Map<String, Object> metadata
) {
    public WorkspaceConnectionDto {
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
