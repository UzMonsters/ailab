package com.ailab.chemistry.domain.solution;

import com.ailab.chemistry.domain.measurement.*;

import java.util.Objects;

public final class SolutionMixingResult {

    private final String soluteCompoundCode;
    private final String solventCompoundCode;
    private final AmountOfSubstance totalSoluteAmount;
    private final Mass totalSoluteMass;
    private final Mass totalSolventMass;
    private final Volume finalSolutionVolume;
    private final MolarConcentration finalMolarity;
    private final Molality finalMolality;
    private final MassConcentration finalMassConcentration;
    private final MassFraction finalMassFraction;
    private final SolutionVolumeAssumption volumeAssumption;

    public SolutionMixingResult(
            String soluteCompoundCode,
            String solventCompoundCode,
            AmountOfSubstance totalSoluteAmount,
            Mass totalSoluteMass,
            Mass totalSolventMass,
            Volume finalSolutionVolume,
            MolarConcentration finalMolarity,
            Molality finalMolality,
            MassConcentration finalMassConcentration,
            MassFraction finalMassFraction,
            SolutionVolumeAssumption volumeAssumption) {
        this.soluteCompoundCode = Objects.requireNonNull(soluteCompoundCode, "Solute compound code must not be null");
        this.solventCompoundCode = Objects.requireNonNull(solventCompoundCode, "Solvent compound code must not be null");
        this.totalSoluteAmount = Objects.requireNonNull(totalSoluteAmount, "Total solute amount must not be null");
        this.totalSoluteMass = Objects.requireNonNull(totalSoluteMass, "Total solute mass must not be null");
        this.totalSolventMass = Objects.requireNonNull(totalSolventMass, "Total solvent mass must not be null");
        this.finalSolutionVolume = Objects.requireNonNull(finalSolutionVolume, "Final solution volume must not be null");
        this.finalMolarity = Objects.requireNonNull(finalMolarity, "Final molarity must not be null");
        this.finalMolality = Objects.requireNonNull(finalMolality, "Final molality must not be null");
        this.finalMassConcentration = Objects.requireNonNull(finalMassConcentration, "Final mass concentration must not be null");
        this.finalMassFraction = Objects.requireNonNull(finalMassFraction, "Final mass fraction must not be null");
        this.volumeAssumption = Objects.requireNonNull(volumeAssumption, "Volume assumption must not be null");
    }

    public String getSoluteCompoundCode() {
        return soluteCompoundCode;
    }

    public String getSolventCompoundCode() {
        return solventCompoundCode;
    }

    public AmountOfSubstance getTotalSoluteAmount() {
        return totalSoluteAmount;
    }

    public Mass getTotalSoluteMass() {
        return totalSoluteMass;
    }

    public Mass getTotalSolventMass() {
        return totalSolventMass;
    }

    public Volume getFinalSolutionVolume() {
        return finalSolutionVolume;
    }

    public MolarConcentration getFinalMolarity() {
        return finalMolarity;
    }

    public Molality getFinalMolality() {
        return finalMolality;
    }

    public MassConcentration getFinalMassConcentration() {
        return finalMassConcentration;
    }

    public MassFraction getFinalMassFraction() {
        return finalMassFraction;
    }

    public SolutionVolumeAssumption getVolumeAssumption() {
        return volumeAssumption;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SolutionMixingResult that = (SolutionMixingResult) o;
        return soluteCompoundCode.equalsIgnoreCase(that.soluteCompoundCode) &&
                solventCompoundCode.equalsIgnoreCase(that.solventCompoundCode) &&
                finalMolarity.equals(that.finalMolarity) &&
                finalSolutionVolume.equals(that.finalSolutionVolume);
    }

    @Override
    public int hashCode() {
        return Objects.hash(soluteCompoundCode.toUpperCase(), solventCompoundCode.toUpperCase(), finalMolarity, finalSolutionVolume);
    }

    @Override
    public String toString() {
        return "SolutionMixingResult{" + soluteCompoundCode + " in " + solventCompoundCode + ": molarity=" + finalMolarity + ", volume=" + finalSolutionVolume + '}';
    }
}
