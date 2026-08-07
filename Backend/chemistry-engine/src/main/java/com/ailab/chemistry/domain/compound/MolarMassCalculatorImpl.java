package com.ailab.chemistry.domain.compound;

import com.ailab.chemistry.domain.element.AtomicMass;
import com.ailab.chemistry.domain.element.AtomicMassKind;

import java.math.BigDecimal;
import java.math.MathContext;

public class MolarMassCalculatorImpl implements MolarMassCalculator {

    public static final String ALGORITHM_VERSION = "v1.0.0-interval-propagation";

    @Override
    public MolarMass calculate(CompoundComposition composition, ElementMassProvider elementMassProvider) {
        if (composition == null || composition.getElementCounts().isEmpty()) {
            throw new CompoundException(CompoundErrorCode.INVALID_COMPOUND_COMPOSITION, "Cannot calculate molar mass for empty composition");
        }
        if (elementMassProvider == null) {
            throw new CompoundException(CompoundErrorCode.ELEMENT_MASS_NOT_FOUND, "ElementMassProvider cannot be null");
        }

        BigDecimal repSum = BigDecimal.ZERO;
        BigDecimal lowerSum = BigDecimal.ZERO;
        BigDecimal upperSum = BigDecimal.ZERO;
        boolean hasInterval = false;
        boolean hasRadioactive = false;

        for (CompoundElementCount count : composition.getElementCounts()) {
            ElementMassData massData = elementMassProvider.getByAtomicNumber(count.getAtomicNumber());
            if (massData == null) {
                throw new CompoundException(CompoundErrorCode.ELEMENT_MASS_NOT_FOUND,
                        "Atomic mass not found for Z=" + count.getAtomicNumber() + " (" + count.getSymbol() + ")");
            }

            BigDecimal multiplier = new BigDecimal(count.getAtomCount());

            BigDecimal repContrib = massData.representativeValue().multiply(multiplier, MathContext.DECIMAL128);
            repSum = repSum.add(repContrib, MathContext.DECIMAL128);

            if (massData.kind() == AtomicMassKind.INTERVAL_STANDARD_ATOMIC_WEIGHT) {
                hasInterval = true;
                BigDecimal lowVal = massData.lowerBound() != null ? massData.lowerBound() : massData.representativeValue();
                BigDecimal highVal = massData.upperBound() != null ? massData.upperBound() : massData.representativeValue();

                lowerSum = lowerSum.add(lowVal.multiply(multiplier, MathContext.DECIMAL128), MathContext.DECIMAL128);
                upperSum = upperSum.add(highVal.multiply(multiplier, MathContext.DECIMAL128), MathContext.DECIMAL128);
            } else {
                lowerSum = lowerSum.add(repContrib, MathContext.DECIMAL128);
                upperSum = upperSum.add(repContrib, MathContext.DECIMAL128);
            }

            if (massData.kind() == AtomicMassKind.RADIOACTIVE_ISOTOPE_MASS_NUMBER || massData.kind() == AtomicMassKind.PREDICTED_OR_PROVISIONAL) {
                hasRadioactive = true;
            }
        }

        MolarMassKind kind;
        if (hasInterval) {
            kind = MolarMassKind.INTERVAL;
        } else if (hasRadioactive) {
            kind = MolarMassKind.RADIOACTIVE_MASS_NUMBER_BASED;
        } else {
            kind = MolarMassKind.EXACT_FROM_FIXED_VALUES;
        }

        MolarMassCalculationBasis basis = new MolarMassCalculationBasis(
                elementMassProvider.getElementDatasetVersion(),
                ALGORITHM_VERSION
        );

        if (hasInterval) {
            return new MolarMass(repSum, lowerSum, upperSum, kind, basis);
        } else {
            return new MolarMass(repSum, null, null, kind, basis);
        }
    }
}
