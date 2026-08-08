package com.ailab.chemistry.domain.kinetics;

import com.ailab.chemistry.domain.measurement.Duration;

import java.math.BigDecimal;
import java.util.Objects;

public record HalfLifeResult(
        String compoundCode,
        Duration halfLife,
        BigDecimal initialConcentrationMolar,
        RateConstant rateConstant,
        String explanation) {
    public HalfLifeResult {
        Objects.requireNonNull(compoundCode, "compoundCode must not be null");
        Objects.requireNonNull(halfLife, "halfLife must not be null");
        Objects.requireNonNull(initialConcentrationMolar, "initialConcentrationMolar must not be null");
        Objects.requireNonNull(rateConstant, "rateConstant must not be null");
    }
}
