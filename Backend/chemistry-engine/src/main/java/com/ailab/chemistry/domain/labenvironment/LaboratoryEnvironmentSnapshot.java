package com.ailab.chemistry.domain.labenvironment;

import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.RelativeHumidity;
import com.ailab.chemistry.domain.measurement.Temperature;

import java.time.Instant;

public record LaboratoryEnvironmentSnapshot(
        Temperature ambientTemperature,
        Pressure ambientPressure,
        RelativeHumidity relativeHumidity,
        VentilationMode ventilationMode,
        FumeHoodState fumeHoodState,
        String atmosphereDeclaration,
        Instant observationTimestamp
) {
}
