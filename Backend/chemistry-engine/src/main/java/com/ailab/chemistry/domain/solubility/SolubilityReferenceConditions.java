package com.ailab.chemistry.domain.solubility;

import com.ailab.chemistry.domain.measurement.Temperature;

import java.util.Objects;

public record SolubilityReferenceConditions(
        Temperature temperature,
        String solventCode,
        String activityConvention
) {
    public SolubilityReferenceConditions {
        Objects.requireNonNull(temperature, "temperature must not be null");
        if (solventCode == null || solventCode.isBlank()) {
            throw new SolubilityException(SolubilityErrorCode.UNSUPPORTED_REFERENCE_CONDITIONS, "Solvent code must not be blank");
        }
        solventCode = solventCode.trim().toUpperCase();
        activityConvention = activityConvention == null ? "" : activityConvention.trim();
    }
}
