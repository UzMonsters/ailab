package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.thermodynamics.ReactionTemperatureCorrectionResult;
import com.ailab.chemistry.domain.thermodynamics.TemperatureCorrectionCoverage;
import com.ailab.chemistry.domain.thermodynamics.TemperatureDependentPropertyResult;

import java.util.List;
import java.util.Map;

public interface TemperatureDependentThermodynamicsService {
    TemperatureDependentPropertyResult calculateSpeciesProperties(String compoundCode, MatterState state, Temperature targetTemperature);

    ReactionTemperatureCorrectionResult calculateReaction(
            String reactionCode,
            Temperature targetTemperature,
            Pressure pressure,
            Map<String, MatterState> stateOverrides);

    List<TemperatureCorrectionCoverage> evaluateCoverage(Temperature targetTemperature);
}
