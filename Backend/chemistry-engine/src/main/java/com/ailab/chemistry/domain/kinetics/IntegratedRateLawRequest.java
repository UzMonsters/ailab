package com.ailab.chemistry.domain.kinetics;

import com.ailab.chemistry.domain.measurement.Duration;
import com.ailab.chemistry.domain.measurement.DurationUnit;

import java.math.BigDecimal;
import java.util.Objects;

public record IntegratedRateLawRequest(
        String compoundCode,
        BigDecimal initialConcentrationMolar,
        RateConstant rateConstant,
        OverallReactionOrder order,
        Duration duration) {
    public IntegratedRateLawRequest {
        Objects.requireNonNull(compoundCode, "compoundCode must not be null");
        Objects.requireNonNull(initialConcentrationMolar, "initialConcentrationMolar must not be null");
        Objects.requireNonNull(rateConstant, "rateConstant must not be null");
        Objects.requireNonNull(order, "order must not be null");
        Objects.requireNonNull(duration, "duration must not be null");

        if (initialConcentrationMolar.compareTo(BigDecimal.ZERO) < 0) {
            throw new KineticException(
                    KineticErrorCode.INVALID_CONCENTRATION,
                    "Initial concentration cannot be negative: " + initialConcentrationMolar);
        }
        if (duration.in(DurationUnit.SECOND).compareTo(BigDecimal.ZERO) < 0) {
            throw new KineticException(
                    KineticErrorCode.INVALID_CONCENTRATION,
                    "Duration cannot be negative: " + duration);
        }
    }
}
