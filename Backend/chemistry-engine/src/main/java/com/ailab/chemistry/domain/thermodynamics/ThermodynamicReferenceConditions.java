package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.Temperature;

public record ThermodynamicReferenceConditions(
        Temperature temperature,
        Pressure pressure,
        MatterState state,
        StandardStateConvention standardStateConvention) {

    public ThermodynamicReferenceConditions {
        if (temperature == null || pressure == null || state == null || state == MatterState.UNKNOWN || standardStateConvention == null) {
            throw new ThermodynamicException(ThermodynamicErrorCode.INVALID_REFERENCE_CONDITIONS,
                    "Temperature, pressure, physical state and standard-state convention are required");
        }
    }
}
