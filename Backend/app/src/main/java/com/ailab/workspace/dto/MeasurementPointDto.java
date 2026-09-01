package com.ailab.workspace.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record MeasurementPointDto(
        String id,
        String kind, // TEMPERATURE, PH, MASS, PRESSURE, VOLUME, VOLTAGE
        BigDecimal value,
        String unit, // degC, K, pH, g, mL, kPa, V
        String sensorId,
        String targetId,
        Instant recordedAt
) {
    public static MeasurementPointDto of(String id, String kind, BigDecimal value, String unit, String sensorId, String targetId) {
        return new MeasurementPointDto(id, kind, value, unit, sensorId, targetId, Instant.now());
    }
}
