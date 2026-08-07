package com.ailab.chemistry.domain.solubility;

import com.ailab.chemistry.domain.measurement.AmountOfSubstance;

import java.util.Objects;

public record SolutionIonAmount(
        String speciesCode,
        AmountOfSubstance amount,
        int charge
) {
    public SolutionIonAmount {
        if (speciesCode == null || speciesCode.isBlank()) {
            throw new SolubilityException(SolubilityErrorCode.INVALID_ION_SPECIES, "Species code is required");
        }
        speciesCode = speciesCode.trim().toUpperCase();
        Objects.requireNonNull(amount, "amount must not be null");
    }
}
