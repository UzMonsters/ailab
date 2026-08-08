package com.ailab.chemistry.domain.acidbase;

import java.math.BigDecimal;
import java.util.Objects;

public record IonicSpeciesConcentration(
        String speciesCode,
        BigDecimal concentration,
        int charge
) {
    public IonicSpeciesConcentration {
        if (speciesCode == null || speciesCode.isBlank()) {
            throw new ActivityException(ActivityErrorCode.INVALID_SPECIES_CHARGE, "Species code must not be blank");
        }
        speciesCode = speciesCode.trim();
        Objects.requireNonNull(concentration, "concentration must not be null");
        if (concentration.compareTo(BigDecimal.ZERO) < 0) {
            throw new ActivityException(ActivityErrorCode.NEGATIVE_CONCENTRATION, "Concentration must not be negative");
        }
    }
}
