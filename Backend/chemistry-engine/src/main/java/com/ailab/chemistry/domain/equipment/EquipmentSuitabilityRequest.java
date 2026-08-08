package com.ailab.chemistry.domain.equipment;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public record EquipmentSuitabilityRequest(
        EquipmentReferenceProfile profile,
        List<EquipmentRequirement> requirements,
        List<CalibrationRecord> calibrationRecords,
        Instant evaluationTimestamp
) {
    public EquipmentSuitabilityRequest {
        Objects.requireNonNull(profile, "profile must not be null");
        requirements = List.copyOf(Objects.requireNonNull(requirements, "requirements must not be null"));
        calibrationRecords = List.copyOf(Objects.requireNonNull(calibrationRecords, "calibrationRecords must not be null"));
        Objects.requireNonNull(evaluationTimestamp, "evaluationTimestamp must not be null");
    }
}
