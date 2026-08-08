package com.ailab.chemistry.domain.kinetics;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

public record RateEvaluationRequest(
        String reactionCode,
        KineticRateLaw rateLaw,
        RateConstant rateConstant,
        Map<String, BigDecimal> concentrations) {
    public RateEvaluationRequest {
        Objects.requireNonNull(reactionCode, "reactionCode must not be null");
        Objects.requireNonNull(rateLaw, "rateLaw must not be null");
        Objects.requireNonNull(rateConstant, "rateConstant must not be null");
        concentrations = concentrations == null ? Map.of() : Map.copyOf(concentrations);

        for (Map.Entry<String, BigDecimal> entry : concentrations.entrySet()) {
            if (entry.getValue() != null && entry.getValue().compareTo(BigDecimal.ZERO) < 0) {
                throw new KineticException(
                        KineticErrorCode.INVALID_CONCENTRATION,
                        "Concentration cannot be negative for " + entry.getKey() + ": " + entry.getValue());
            }
        }
    }
}
