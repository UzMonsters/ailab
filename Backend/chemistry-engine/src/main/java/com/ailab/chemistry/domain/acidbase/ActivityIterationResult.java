package com.ailab.chemistry.domain.acidbase;

import java.math.BigDecimal;
import java.util.Objects;

public record ActivityIterationResult(
        int iterationCount,
        BigDecimal hydroniumDelta,
        BigDecimal ionicStrengthDelta,
        BigDecimal maximumActivityCoefficientDelta
) {
    public ActivityIterationResult {
        Objects.requireNonNull(hydroniumDelta, "hydroniumDelta must not be null");
        Objects.requireNonNull(ionicStrengthDelta, "ionicStrengthDelta must not be null");
        Objects.requireNonNull(maximumActivityCoefficientDelta, "maximumActivityCoefficientDelta must not be null");
    }
}
