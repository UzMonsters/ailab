package com.ailab.chemistry.domain.solution;

import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.Volume;

import java.util.Objects;

public final class DilutionResult {

    private final String soluteCompoundCode;
    private final MolarConcentration initialConcentration;
    private final Volume initialVolume;
    private final MolarConcentration targetConcentration;
    private final Volume targetVolume;
    private final Volume requiredAddedSolventVolume;
    private final AmountOfSubstance soluteAmount;

    public DilutionResult(
            String soluteCompoundCode,
            MolarConcentration initialConcentration,
            Volume initialVolume,
            MolarConcentration targetConcentration,
            Volume targetVolume,
            Volume requiredAddedSolventVolume,
            AmountOfSubstance soluteAmount) {
        this.soluteCompoundCode = Objects.requireNonNull(soluteCompoundCode, "Solute compound code must not be null");
        this.initialConcentration = Objects.requireNonNull(initialConcentration, "Initial concentration must not be null");
        this.initialVolume = Objects.requireNonNull(initialVolume, "Initial volume must not be null");
        this.targetConcentration = Objects.requireNonNull(targetConcentration, "Target concentration must not be null");
        this.targetVolume = Objects.requireNonNull(targetVolume, "Target volume must not be null");
        this.requiredAddedSolventVolume = Objects.requireNonNull(requiredAddedSolventVolume, "Required added solvent volume must not be null");
        this.soluteAmount = Objects.requireNonNull(soluteAmount, "Solute amount must not be null");
    }

    public String getSoluteCompoundCode() {
        return soluteCompoundCode;
    }

    public MolarConcentration getInitialConcentration() {
        return initialConcentration;
    }

    public Volume getInitialVolume() {
        return initialVolume;
    }

    public MolarConcentration getTargetConcentration() {
        return targetConcentration;
    }

    public Volume getTargetVolume() {
        return targetVolume;
    }

    public Volume getRequiredAddedSolventVolume() {
        return requiredAddedSolventVolume;
    }

    public AmountOfSubstance getSoluteAmount() {
        return soluteAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DilutionResult that = (DilutionResult) o;
        return soluteCompoundCode.equalsIgnoreCase(that.soluteCompoundCode) &&
                initialConcentration.equals(that.initialConcentration) &&
                initialVolume.equals(that.initialVolume) &&
                targetConcentration.equals(that.targetConcentration) &&
                targetVolume.equals(that.targetVolume);
    }

    @Override
    public int hashCode() {
        return Objects.hash(soluteCompoundCode.toUpperCase(), initialConcentration, initialVolume, targetConcentration, targetVolume);
    }

    @Override
    public String toString() {
        return "DilutionResult{" + soluteCompoundCode + ": C1=" + initialConcentration + ", V1=" + initialVolume + " -> C2=" + targetConcentration + ", V2=" + targetVolume + ", AddedSolvent=" + requiredAddedSolventVolume + '}';
    }
}
