package com.ailab.chemistry.domain.stoichiometry;

import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.AmountOfSubstanceUnit;
import com.ailab.chemistry.domain.measurement.Mass;

import java.math.BigDecimal;
import java.util.Objects;

public final class ExcessReactantResult {

    private final String compoundCode;
    private final AmountOfSubstance initialMoles;
    private final AmountOfSubstance consumedMoles;
    private final AmountOfSubstance remainingMoles;
    private final Mass remainingMass;
    private final BigDecimal excessPercentage;

    public ExcessReactantResult(String compoundCode, AmountOfSubstance initialMoles, AmountOfSubstance consumedMoles, AmountOfSubstance remainingMoles, Mass remainingMass, BigDecimal excessPercentage) {
        this.compoundCode = Objects.requireNonNull(compoundCode, "Compound code must not be null").trim();
        this.initialMoles = Objects.requireNonNull(initialMoles, "Initial moles must not be null");
        this.consumedMoles = Objects.requireNonNull(consumedMoles, "Consumed moles must not be null");
        this.remainingMoles = Objects.requireNonNull(remainingMoles, "Remaining moles must not be null");
        this.remainingMass = Objects.requireNonNull(remainingMass, "Remaining mass must not be null");
        this.excessPercentage = Objects.requireNonNull(excessPercentage, "Excess percentage must not be null");
    }

    public String getCompoundCode() {
        return compoundCode;
    }

    public AmountOfSubstance getInitialMoles() {
        return initialMoles;
    }

    public AmountOfSubstance getConsumedMoles() {
        return consumedMoles;
    }

    public AmountOfSubstance getRemainingMoles() {
        return remainingMoles;
    }

    public Mass getRemainingMass() {
        return remainingMass;
    }

    public BigDecimal getExcessPercentage() {
        return excessPercentage;
    }

    public boolean isCompletelyConsumed() {
        return remainingMoles.in(AmountOfSubstanceUnit.MOLE).compareTo(BigDecimal.ZERO) == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExcessReactantResult that = (ExcessReactantResult) o;
        return compoundCode.equalsIgnoreCase(that.compoundCode) &&
                initialMoles.equals(that.initialMoles) &&
                consumedMoles.equals(that.consumedMoles) &&
                remainingMoles.equals(that.remainingMoles) &&
                remainingMass.equals(that.remainingMass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(compoundCode.toUpperCase(), initialMoles, consumedMoles, remainingMoles, remainingMass);
    }

    @Override
    public String toString() {
        return "ExcessReactantResult{" + compoundCode + ": remaining=" + remainingMoles + " (" + remainingMass + ")}";
    }
}
