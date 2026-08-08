package com.ailab.chemistry.domain.kinetics;

import java.math.BigDecimal;
import java.util.Objects;

public record KineticProfile(
        String profileId,
        String reactionCode,
        KineticRateLaw rateLaw,
        RateConstant referenceRateConstant,
        ArrheniusParameters arrheniusParameters,
        KineticReferenceConditions conditions,
        KineticEvidenceStatus evidenceStatus,
        KineticProvenance provenance) {
    public KineticProfile {
        Objects.requireNonNull(profileId, "profileId must not be null");
        Objects.requireNonNull(reactionCode, "reactionCode must not be null");
        Objects.requireNonNull(rateLaw, "rateLaw must not be null");
        Objects.requireNonNull(referenceRateConstant, "referenceRateConstant must not be null");
        Objects.requireNonNull(evidenceStatus, "evidenceStatus must not be null");

        // Validate rate constant dimension against rate law overall order
        BigDecimal orderVal = rateLaw.overallOrder().totalOrderValue();
        BigDecimal dimOrderVal = referenceRateConstant.dimension().order();
        if (orderVal.compareTo(dimOrderVal) != 0) {
            throw new KineticException(
                    KineticErrorCode.DIMENSIONAL_MISMATCH,
                    "Rate constant dimension order " + dimOrderVal + " does not match rate law overall order " + orderVal);
        }
    }
}
