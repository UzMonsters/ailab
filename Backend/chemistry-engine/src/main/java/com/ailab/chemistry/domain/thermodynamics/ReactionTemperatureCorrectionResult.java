package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.Temperature;

import java.util.List;
import java.util.Map;

public record ReactionTemperatureCorrectionResult(
        String reactionCode,
        Temperature targetTemperature,
        Pressure pressure,
        TemperatureCorrectionStatus status,
        TemperatureCorrectionCoverage coverage,
        Map<ReactionThermodynamicProperty, ReactionThermodynamicResultProperty> reactionProperties,
        List<SpeciesTemperatureCorrection> speciesCorrections,
        ReactionThermodynamicsResult referenceResult,
        TemperatureCorrectionMethod method,
        String explanation) {

    public ReactionTemperatureCorrectionResult {
        reactionProperties = Map.copyOf(reactionProperties);
        speciesCorrections = List.copyOf(speciesCorrections);
    }
}
