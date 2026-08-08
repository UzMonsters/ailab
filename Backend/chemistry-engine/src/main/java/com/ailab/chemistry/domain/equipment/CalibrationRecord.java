package com.ailab.chemistry.domain.equipment;

import java.time.Instant;
import java.util.Objects;

public record CalibrationRecord(String recordId, Instant calibratedAt, String source) {
    public CalibrationRecord {
        Objects.requireNonNull(recordId, "recordId must not be null");
        Objects.requireNonNull(calibratedAt, "calibratedAt must not be null");
        Objects.requireNonNull(source, "source must not be null");
    }
}
