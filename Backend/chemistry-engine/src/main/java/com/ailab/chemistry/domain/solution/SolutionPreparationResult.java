package com.ailab.chemistry.domain.solution;

import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.Mass;
import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.Volume;

import java.util.Objects;
import java.util.Optional;

public final class SolutionPreparationResult {

    private final String soluteCompoundCode;
    private final MolarConcentration targetConcentration;
    private final Volume targetVolume;
    private final Mass requiredSoluteMass;
    private final Mass requiredSoluteMassLowerBound;
    private final Mass requiredSoluteMassUpperBound;
    private final AmountOfSubstance requiredSoluteAmount;
    private final AmountOfSubstance requiredSoluteAmountLowerBound;
    private final AmountOfSubstance requiredSoluteAmountUpperBound;

    public SolutionPreparationResult(
            String soluteCompoundCode,
            MolarConcentration targetConcentration,
            Volume targetVolume,
            Mass requiredSoluteMass,
            Mass requiredSoluteMassLowerBound,
            Mass requiredSoluteMassUpperBound,
            AmountOfSubstance requiredSoluteAmount,
            AmountOfSubstance requiredSoluteAmountLowerBound,
            AmountOfSubstance requiredSoluteAmountUpperBound) {
        this.soluteCompoundCode = Objects.requireNonNull(soluteCompoundCode, "Solute compound code must not be null");
        this.targetConcentration = Objects.requireNonNull(targetConcentration, "Target concentration must not be null");
        this.targetVolume = Objects.requireNonNull(targetVolume, "Target volume must not be null");
        this.requiredSoluteMass = Objects.requireNonNull(requiredSoluteMass, "Required solute mass must not be null");
        this.requiredSoluteMassLowerBound = requiredSoluteMassLowerBound;
        this.requiredSoluteMassUpperBound = requiredSoluteMassUpperBound;
        this.requiredSoluteAmount = Objects.requireNonNull(requiredSoluteAmount, "Required solute amount must not be null");
        this.requiredSoluteAmountLowerBound = requiredSoluteAmountLowerBound;
        this.requiredSoluteAmountUpperBound = requiredSoluteAmountUpperBound;
    }

    public String getSoluteCompoundCode() {
        return soluteCompoundCode;
    }

    public MolarConcentration getTargetConcentration() {
        return targetConcentration;
    }

    public Volume getTargetVolume() {
        return targetVolume;
    }

    public Mass getRequiredSoluteMass() {
        return requiredSoluteMass;
    }

    public Optional<Mass> getRequiredSoluteMassLowerBound() {
        return Optional.ofNullable(requiredSoluteMassLowerBound);
    }

    public Optional<Mass> getRequiredSoluteMassUpperBound() {
        return Optional.ofNullable(requiredSoluteMassUpperBound);
    }

    public AmountOfSubstance getRequiredSoluteAmount() {
        return requiredSoluteAmount;
    }

    public Optional<AmountOfSubstance> getRequiredSoluteAmountLowerBound() {
        return Optional.ofNullable(requiredSoluteAmountLowerBound);
    }

    public Optional<AmountOfSubstance> getRequiredSoluteAmountUpperBound() {
        return Optional.ofNullable(requiredSoluteAmountUpperBound);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SolutionPreparationResult that = (SolutionPreparationResult) o;
        return soluteCompoundCode.equalsIgnoreCase(that.soluteCompoundCode) &&
                targetConcentration.equals(that.targetConcentration) &&
                targetVolume.equals(that.targetVolume) &&
                requiredSoluteMass.equals(that.requiredSoluteMass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(soluteCompoundCode.toUpperCase(), targetConcentration, targetVolume, requiredSoluteMass);
    }

    @Override
    public String toString() {
        return "SolutionPreparationResult{" + soluteCompoundCode + ": " + requiredSoluteMass + " for " + targetVolume + " of " + targetConcentration + '}';
    }
}
