package com.ailab.chemistry.domain.solution;

import com.ailab.chemistry.domain.measurement.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

public final class SolutionComposition {

    private final String soluteCode;
    private final String solventCode;
    private final Mass soluteMass;
    private final AmountOfSubstance soluteAmount;
    private final Mass solventMass;
    private final AmountOfSubstance solventAmount;
    private final Volume solventVolume;
    private final Volume solutionVolume;
    private final Mass totalSolutionMass;
    private final Density density;

    public SolutionComposition(
            String soluteCode,
            String solventCode,
            Mass soluteMass,
            AmountOfSubstance soluteAmount,
            Mass solventMass,
            AmountOfSubstance solventAmount,
            Volume solventVolume,
            Volume solutionVolume,
            Density density) {
        if (soluteCode == null || soluteCode.isBlank() || solventCode == null || solventCode.isBlank()) {
            throw new SolutionException(SolutionErrorCode.COMPOUND_NOT_FOUND, "Solute and solvent compound codes must not be null or blank");
        }
        if (soluteCode.trim().equalsIgnoreCase(solventCode.trim())) {
            throw new SolutionException(SolutionErrorCode.SOLUTE_EQUALS_SOLVENT, "Solute and solvent cannot be the same compound: " + soluteCode);
        }
        this.soluteCode = soluteCode.trim();
        this.solventCode = solventCode.trim();
        this.soluteMass = Objects.requireNonNull(soluteMass, "Solute mass must not be null");
        this.soluteAmount = Objects.requireNonNull(soluteAmount, "Solute amount must not be null");
        this.solventMass = Objects.requireNonNull(solventMass, "Solvent mass must not be null");
        this.solventAmount = Objects.requireNonNull(solventAmount, "Solvent amount must not be null");
        this.solventVolume = solventVolume;
        this.solutionVolume = solutionVolume;
        this.totalSolutionMass = soluteMass.add(solventMass);
        this.density = density;
    }

    public String getSoluteCode() {
        return soluteCode;
    }

    public String getSolventCode() {
        return solventCode;
    }

    public Mass getSoluteMass() {
        return soluteMass;
    }

    public AmountOfSubstance getSoluteAmount() {
        return soluteAmount;
    }

    public Mass getSolventMass() {
        return solventMass;
    }

    public AmountOfSubstance getSolventAmount() {
        return solventAmount;
    }

    public Optional<Volume> getSolventVolume() {
        return Optional.ofNullable(solventVolume);
    }

    public Optional<Volume> getSolutionVolume() {
        return Optional.ofNullable(solutionVolume);
    }

    public Mass getTotalSolutionMass() {
        return totalSolutionMass;
    }

    public Optional<Density> getDensity() {
        return Optional.ofNullable(density);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SolutionComposition that = (SolutionComposition) o;
        return soluteCode.equalsIgnoreCase(that.soluteCode) &&
                solventCode.equalsIgnoreCase(that.solventCode) &&
                soluteMass.equals(that.soluteMass) &&
                soluteAmount.equals(that.soluteAmount) &&
                solventMass.equals(that.solventMass) &&
                solventAmount.equals(that.solventAmount) &&
                Objects.equals(solutionVolume, that.solutionVolume);
    }

    @Override
    public int hashCode() {
        return Objects.hash(soluteCode.toUpperCase(), solventCode.toUpperCase(), soluteMass, soluteAmount, solventMass, solventAmount, solutionVolume);
    }

    @Override
    public String toString() {
        return "SolutionComposition{" + soluteCode + " in " + solventCode + ", soluteMass=" + soluteMass + ", totalMass=" + totalSolutionMass + '}';
    }
}
