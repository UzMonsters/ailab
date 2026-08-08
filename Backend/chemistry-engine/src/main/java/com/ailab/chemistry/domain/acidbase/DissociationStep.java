package com.ailab.chemistry.domain.acidbase;

import java.util.Objects;
import java.util.Optional;

public final class DissociationStep {

    private final String acidSpeciesCode;
    private final String deprotonatedSpeciesCode;
    private final int stepNumber;
    private final EquilibriumConstant kaConstant;

    public DissociationStep(String acidSpeciesCode, String deprotonatedSpeciesCode, int stepNumber, EquilibriumConstant kaConstant) {
        if (acidSpeciesCode == null || acidSpeciesCode.isBlank() || deprotonatedSpeciesCode == null || deprotonatedSpeciesCode.isBlank()) {
            throw new AcidBaseException(AcidBaseErrorCode.ORDERED_STEP_MISMATCH, "Acid species code and deprotonated species code must not be null or blank");
        }
        if (stepNumber < 1) {
            throw new AcidBaseException(AcidBaseErrorCode.ORDERED_STEP_MISMATCH, "Step number must be at least 1: " + stepNumber);
        }
        this.acidSpeciesCode = acidSpeciesCode.trim();
        this.deprotonatedSpeciesCode = deprotonatedSpeciesCode.trim();
        this.stepNumber = stepNumber;
        this.kaConstant = kaConstant;
    }

    public String getAcidSpeciesCode() {
        return acidSpeciesCode;
    }

    public String getDeprotonatedSpeciesCode() {
        return deprotonatedSpeciesCode;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public Optional<EquilibriumConstant> getKaConstant() {
        return Optional.ofNullable(kaConstant);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DissociationStep that = (DissociationStep) o;
        return stepNumber == that.stepNumber &&
                acidSpeciesCode.equalsIgnoreCase(that.acidSpeciesCode) &&
                deprotonatedSpeciesCode.equalsIgnoreCase(that.deprotonatedSpeciesCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(acidSpeciesCode.toUpperCase(), deprotonatedSpeciesCode.toUpperCase(), stepNumber);
    }

    @Override
    public String toString() {
        return "Step " + stepNumber + ": " + acidSpeciesCode + " -> " + deprotonatedSpeciesCode + " + H+ (" + (kaConstant != null ? kaConstant : "N/A") + ")";
    }
}
