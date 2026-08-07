package com.ailab.chemistry.domain.stoichiometry;

import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.Mass;

import java.util.Objects;

public final class ActualYieldResult {

    private final TheoreticalYieldResult theoreticalYield;
    private final Mass actualMass;
    private final AmountOfSubstance actualMoles;
    private final PercentYield percentYield;

    public ActualYieldResult(TheoreticalYieldResult theoreticalYield, Mass actualMass, AmountOfSubstance actualMoles, PercentYield percentYield) {
        this.theoreticalYield = Objects.requireNonNull(theoreticalYield, "TheoreticalYieldResult must not be null");
        this.actualMass = Objects.requireNonNull(actualMass, "Actual mass must not be null");
        this.actualMoles = Objects.requireNonNull(actualMoles, "Actual moles must not be null");
        this.percentYield = Objects.requireNonNull(percentYield, "PercentYield must not be null");
    }

    public TheoreticalYieldResult getTheoreticalYield() {
        return theoreticalYield;
    }

    public Mass getActualMass() {
        return actualMass;
    }

    public AmountOfSubstance getActualMoles() {
        return actualMoles;
    }

    public PercentYield getPercentYield() {
        return percentYield;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActualYieldResult that = (ActualYieldResult) o;
        return theoreticalYield.equals(that.theoreticalYield) &&
                actualMass.equals(that.actualMass) &&
                percentYield.equals(that.percentYield);
    }

    @Override
    public int hashCode() {
        return Objects.hash(theoreticalYield, actualMass, percentYield);
    }

    @Override
    public String toString() {
        return "ActualYieldResult{actualMass=" + actualMass + ", percentYield=" + percentYield + '}';
    }
}
