package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.Temperature;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public record ReactionThermodynamicsRequest(
        String reactionCode,
        String equation,
        List<ReactionThermodynamicRequestTerm> terms,
        Temperature temperature,
        Pressure pressure,
        Map<String, ReactionThermodynamicRecordSet> recordSets) {

    public ReactionThermodynamicsRequest {
        Objects.requireNonNull(reactionCode, "reactionCode must not be null");
        Objects.requireNonNull(equation, "equation must not be null");
        Objects.requireNonNull(terms, "terms must not be null");
        Objects.requireNonNull(temperature, "temperature must not be null");
        Objects.requireNonNull(pressure, "pressure must not be null");
        Objects.requireNonNull(recordSets, "recordSets must not be null");
        terms = List.copyOf(terms);
        recordSets = Map.copyOf(recordSets);
    }
}
