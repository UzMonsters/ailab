package com.ailab.chemistry.api;

import java.util.Objects;

public final class DissociationStepDetails {

    private final String acidSpeciesCode;
    private final String deprotonatedSpeciesCode;
    private final int stepNumber;
    private final EquilibriumConstantDetails kaConstant;

    public DissociationStepDetails(String acidSpeciesCode, String deprotonatedSpeciesCode, int stepNumber, EquilibriumConstantDetails kaConstant) {
        this.acidSpeciesCode = Objects.requireNonNull(acidSpeciesCode);
        this.deprotonatedSpeciesCode = Objects.requireNonNull(deprotonatedSpeciesCode);
        this.stepNumber = stepNumber;
        this.kaConstant = kaConstant;
    }

    public String getAcidSpeciesCode() { return acidSpeciesCode; }
    public String getDeprotonatedSpeciesCode() { return deprotonatedSpeciesCode; }
    public int getStepNumber() { return stepNumber; }
    public EquilibriumConstantDetails getKaConstant() { return kaConstant; }
}
