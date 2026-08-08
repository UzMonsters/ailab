package com.ailab.chemistry.domain.equipment;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public record EquipmentProfileSuitabilityRequest(
        String profileId,
        List<EquipmentRequirement> requirements,
        List<CalibrationRecord> calibrationRecords,
        Instant evaluationTimestamp
) {
    public EquipmentProfileSuitabilityRequest {
        Objects.requireNonNull(profileId, "profileId must not be null");
        requirements = List.copyOf(Objects.requireNonNull(requirements, "requirements must not be null"));
        calibrationRecords = List.copyOf(Objects.requireNonNull(calibrationRecords, "calibrationRecords must not be null"));
        Objects.requireNonNull(evaluationTimestamp, "evaluationTimestamp must not be null");
    }
}
