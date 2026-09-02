package com.ailab.workspace.dto;

import java.util.Map;

public record CheckpointFactDto(
        String type, // CONNECTION_COMPLETED, DILUTION_COMPLETED, MIX_COMPLETED, NEUTRALIZATION_COMPLETED, GAS_EVOLVED, TARGET_TEMPERATURE_REACHED, MEASUREMENT_RECORDED
        String vesselId,
        String description,
        Map<String, Object> details
) {
    public CheckpointFactDto {
        details = details == null ? Map.of() : Map.copyOf(details);
    }

    public static CheckpointFactDto of(String type, String vesselId, String description) {
        return new CheckpointFactDto(type, vesselId, description, Map.of());
    }

    public static CheckpointFactDto of(String type, String vesselId, String description, Map<String, Object> details) {
        return new CheckpointFactDto(type, vesselId, description, details);
    }
}
