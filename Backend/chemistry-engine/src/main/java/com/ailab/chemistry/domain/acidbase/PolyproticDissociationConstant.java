package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.Temperature;

import java.math.BigDecimal;
import java.util.Objects;

public record PolyproticDissociationConstant(
        int stepNumber,
        BigDecimal value,
        Temperature temperature,
        String solventCode
) {
    public PolyproticDissociationConstant {
        if (stepNumber < 1) {
            throw new PolyproticException(PolyproticErrorCode.NONCONTIGUOUS_DISSOCIATION_STEPS, "Step number must be at least 1");
        }
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PolyproticException(PolyproticErrorCode.MISSING_KA_STEP, "Ka value must be positive");
        }
        Objects.requireNonNull(temperature, "temperature must not be null");
        if (solventCode == null || solventCode.isBlank()) {
            throw new PolyproticException(PolyproticErrorCode.UNSUPPORTED_SOLVENT, "Solvent code must not be blank");
        }
        solventCode = solventCode.trim();
    }
}
