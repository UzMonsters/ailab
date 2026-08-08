package com.ailab.chemistry.domain.compound;

import java.math.BigDecimal;
import java.util.Objects;

public final class MolarMass {
    public static final String CANONICAL_UNIT = "g/mol";

    private final BigDecimal representativeValue;
    private final BigDecimal lowerBound;
    private final BigDecimal upperBound;
    private final MolarMassKind kind;
    private final MolarMassCalculationBasis calculationBasis;

    public MolarMass(BigDecimal representativeValue, BigDecimal lowerBound, BigDecimal upperBound, MolarMassKind kind, MolarMassCalculationBasis calculationBasis) {
        if (representativeValue == null || representativeValue.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CompoundException(CompoundErrorCode.INVALID_MOLAR_MASS, "Molar mass representative value must be strictly positive (> 0)");
        }
        if (kind == null) {
            throw new CompoundException(CompoundErrorCode.INVALID_MOLAR_MASS, "MolarMassKind cannot be null");
        }
        if (calculationBasis == null) {
            throw new CompoundException(CompoundErrorCode.INVALID_MOLAR_MASS, "CalculationBasis cannot be null");
        }

        if (lowerBound != null && upperBound != null) {
            if (lowerBound.compareTo(upperBound) > 0) {
                throw new CompoundException(CompoundErrorCode.MOLAR_MASS_INTERVAL_INVALID,
                        "Molar mass lower bound (" + lowerBound + ") cannot exceed upper bound (" + upperBound + ")");
            }
            if (representativeValue.compareTo(lowerBound) < 0 || representativeValue.compareTo(upperBound) > 0) {
                throw new CompoundException(CompoundErrorCode.MOLAR_MASS_INTERVAL_INVALID,
                        "Molar mass representative value (" + representativeValue + ") must lie within [" + lowerBound + ", " + upperBound + "]");
            }
        } else if (lowerBound != null || upperBound != null) {
            throw new CompoundException(CompoundErrorCode.MOLAR_MASS_INTERVAL_INVALID,
                    "Both lower bound and upper bound must be provided together for interval molar mass");
        }

        this.representativeValue = representativeValue.stripTrailingZeros();
        this.lowerBound = lowerBound != null ? lowerBound.stripTrailingZeros() : null;
        this.upperBound = upperBound != null ? upperBound.stripTrailingZeros() : null;
        this.kind = kind;
        this.calculationBasis = calculationBasis;
    }

    public static MolarMass exact(BigDecimal representativeValue, MolarMassCalculationBasis basis) {
        return new MolarMass(representativeValue, null, null, MolarMassKind.EXACT_FROM_FIXED_VALUES, basis);
    }

    public static MolarMass interval(BigDecimal representativeValue, BigDecimal lowerBound, BigDecimal upperBound, MolarMassCalculationBasis basis) {
        return new MolarMass(representativeValue, lowerBound, upperBound, MolarMassKind.INTERVAL, basis);
    }

    public BigDecimal getRepresentativeValue() {
        return representativeValue;
    }

    public BigDecimal getLowerBound() {
        return lowerBound;
    }

    public BigDecimal getUpperBound() {
        return upperBound;
    }

    public boolean isInterval() {
        return lowerBound != null && upperBound != null;
    }

    public MolarMassKind getKind() {
        return kind;
    }

    public MolarMassCalculationBasis getCalculationBasis() {
        return calculationBasis;
    }

    public String getUnit() {
        return CANONICAL_UNIT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MolarMass molarMass = (MolarMass) o;
        return representativeValue.compareTo(molarMass.representativeValue) == 0 &&
               ((lowerBound == null && molarMass.lowerBound == null) || (lowerBound != null && molarMass.lowerBound != null && lowerBound.compareTo(molarMass.lowerBound) == 0)) &&
               ((upperBound == null && molarMass.upperBound == null) || (upperBound != null && molarMass.upperBound != null && upperBound.compareTo(molarMass.upperBound) == 0)) &&
               kind == molarMass.kind &&
               Objects.equals(calculationBasis, molarMass.calculationBasis);
    }

    @Override
    public int hashCode() {
        return Objects.hash(representativeValue, lowerBound, upperBound, kind, calculationBasis);
    }

    @Override
    public String toString() {
        if (isInterval()) {
            return representativeValue.toPlainString() + " g/mol [" + lowerBound.toPlainString() + ", " + upperBound.toPlainString() + "] (" + kind + ")";
        }
        return representativeValue.toPlainString() + " g/mol (" + kind + ")";
    }
}
