package com.ailab.chemistry.domain.acidbase;

import java.math.BigDecimal;
import java.util.Objects;

public record ChemicalActivity(
        String speciesCode,
        BigDecimal activity
) {
    public ChemicalActivity {
        if (speciesCode == null || speciesCode.isBlank()) {
            throw new ActivityException(ActivityErrorCode.INVALID_SPECIES_CHARGE, "Species code must not be blank");
        }
        speciesCode = speciesCode.trim();
        Objects.requireNonNull(activity, "activity must not be null");
        if (activity.compareTo(BigDecimal.ZERO) < 0 || !Double.isFinite(activity.doubleValue())) {
            throw new ActivityException(ActivityErrorCode.NON_FINITE_ACTIVITY_COEFFICIENT, "Activity must be non-negative and finite");
        }
    }
}
