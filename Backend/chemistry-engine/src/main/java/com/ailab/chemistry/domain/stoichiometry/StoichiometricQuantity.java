package com.ailab.chemistry.domain.stoichiometry;

import com.ailab.chemistry.domain.compound.MolarMass;
import com.ailab.chemistry.domain.measurement.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

public final class StoichiometricQuantity {

    private final Mass mass;
    private final AmountOfSubstance moles;
    private final Purity purity;

    private StoichiometricQuantity(Mass mass, AmountOfSubstance moles, Purity purity) {
        if (mass == null && moles == null) {
            throw new StoichiometryException(StoichiometryErrorCode.INVALID_QUANTITY, "Either mass or moles must be provided");
        }
        this.mass = mass;
        this.moles = moles;
        this.purity = purity != null ? purity : Purity.PURE;
    }

    public static StoichiometricQuantity fromMass(Mass mass, Purity purity) {
        return new StoichiometricQuantity(Objects.requireNonNull(mass, "Mass must not be null"), null, purity);
    }

    public static StoichiometricQuantity fromMass(Mass mass) {
        return fromMass(mass, Purity.PURE);
    }

    public static StoichiometricQuantity fromMoles(AmountOfSubstance moles, Purity purity) {
        return new StoichiometricQuantity(null, Objects.requireNonNull(moles, "Moles must not be null"), purity);
    }

    public static StoichiometricQuantity fromMoles(AmountOfSubstance moles) {
        return fromMoles(moles, Purity.PURE);
    }

    public Optional<Mass> getMass() {
        return Optional.ofNullable(mass);
    }

    public Optional<AmountOfSubstance> getMoles() {
        return Optional.ofNullable(moles);
    }

    public Purity getPurity() {
        return purity;
    }

    public AmountOfSubstance toPureMoles(MolarMass molarMass) {
        Objects.requireNonNull(molarMass, "MolarMass must not be null");
        BigDecimal purityFactor = purity.getFraction();

        if (moles != null) {
            BigDecimal pureVal = moles.in(AmountOfSubstanceUnit.MOLE).multiply(purityFactor, ScientificMath.CALCULATION_CONTEXT);
            return AmountOfSubstance.of(pureVal, AmountOfSubstanceUnit.MOLE);
        } else {
            BigDecimal rawGrams = mass.in(MassUnit.GRAM);
            BigDecimal pureGrams = rawGrams.multiply(purityFactor, ScientificMath.CALCULATION_CONTEXT);
            BigDecimal repMolarMass = molarMass.getRepresentativeValue();
            if (repMolarMass.compareTo(BigDecimal.ZERO) <= 0) {
                throw new StoichiometryException(StoichiometryErrorCode.CALCULATION_ERROR, "Invalid representative molar mass: " + repMolarMass);
            }
            BigDecimal molesVal = pureGrams.divide(repMolarMass, ScientificMath.CALCULATION_CONTEXT);
            return AmountOfSubstance.of(molesVal, AmountOfSubstanceUnit.MOLE);
        }
    }

    public AmountOfSubstance toPureMolesLowerBound(MolarMass molarMass) {
        if (molarMass == null || molarMass.getUpperBound() == null) {
            return toPureMoles(molarMass);
        }
        BigDecimal purityFactor = purity.getFraction();
        if (moles != null) {
            return toPureMoles(molarMass);
        } else {
            BigDecimal pureGrams = mass.in(MassUnit.GRAM).multiply(purityFactor, ScientificMath.CALCULATION_CONTEXT);
            BigDecimal molesVal = pureGrams.divide(molarMass.getUpperBound(), ScientificMath.CALCULATION_CONTEXT);
            return AmountOfSubstance.of(molesVal, AmountOfSubstanceUnit.MOLE);
        }
    }

    public AmountOfSubstance toPureMolesUpperBound(MolarMass molarMass) {
        if (molarMass == null || molarMass.getLowerBound() == null) {
            return toPureMoles(molarMass);
        }
        BigDecimal purityFactor = purity.getFraction();
        if (moles != null) {
            return toPureMoles(molarMass);
        } else {
            BigDecimal pureGrams = mass.in(MassUnit.GRAM).multiply(purityFactor, ScientificMath.CALCULATION_CONTEXT);
            BigDecimal molesVal = pureGrams.divide(molarMass.getLowerBound(), ScientificMath.CALCULATION_CONTEXT);
            return AmountOfSubstance.of(molesVal, AmountOfSubstanceUnit.MOLE);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoichiometricQuantity that = (StoichiometricQuantity) o;
        return Objects.equals(mass, that.mass) && Objects.equals(moles, that.moles) && purity.equals(that.purity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mass, moles, purity);
    }

    @Override
    public String toString() {
        String mainVal = mass != null ? mass.toString() : (moles != null ? moles.toString() : "");
        return mainVal + (purity.equals(Purity.PURE) ? "" : " (" + purity + ")");
    }
}
