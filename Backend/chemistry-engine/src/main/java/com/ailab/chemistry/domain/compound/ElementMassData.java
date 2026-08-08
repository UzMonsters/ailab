package com.ailab.chemistry.domain.compound;

import com.ailab.chemistry.domain.element.AtomicMass;
import com.ailab.chemistry.domain.element.AtomicMassKind;

import java.math.BigDecimal;

public record ElementMassData(
        int atomicNumber,
        BigDecimal representativeValue,
        BigDecimal lowerBound,
        BigDecimal upperBound,
        AtomicMassKind kind,
        String datasetVersion,
        String sourceIdentifier
) {
    public AtomicMass toAtomicMass() {
        return new AtomicMass(representativeValue, kind, lowerBound, upperBound);
    }
}
