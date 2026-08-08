package com.ailab.chemistry.domain.phasebehavior;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Temperature;

import java.math.BigDecimal;

public record AntoineCoefficientSet(
        String correlationId,
        String compoundCode,
        MatterState initialPhase,
        MatterState finalPhase,
        BigDecimal a,
        BigDecimal b,
        BigDecimal c,
        Temperature minTemperature,
        Temperature maxTemperature,
        String temperatureUnit,
        String pressureUnit,
        String convention,
        PhaseTransitionProvenance provenance
) implements VaporPressureCorrelation {
}
