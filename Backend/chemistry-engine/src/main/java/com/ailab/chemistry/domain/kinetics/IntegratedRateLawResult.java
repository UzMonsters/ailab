package com.ailab.chemistry.domain.kinetics;

import com.ailab.chemistry.domain.measurement.Duration;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public record IntegratedRateLawResult(
        String compoundCode,
        BigDecimal initialConcentrationMolar,
        BigDecimal finalConcentrationMolar,
        Duration duration,
        BigDecimal fractionRemaining,
        KineticCalculationMethod method,
        String explanation,
        List<String> assumptions) {
    public IntegratedRateLawResult {
        Objects.requireNonNull(compoundCode, "compoundCode must not be null");
        Objects.requireNonNull(initialConcentrationMolar, "initialConcentrationMolar must not be null");
        Objects.requireNonNull(finalConcentrationMolar, "finalConcentrationMolar must not be null");
        Objects.requireNonNull(duration, "duration must not be null");
        assumptions = assumptions == null ? List.of() : List.copyOf(assumptions);
    }
}
