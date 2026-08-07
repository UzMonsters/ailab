package com.ailab.chemistry.domain.acidbase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.UUID;

public final class EquilibriumConstant {

    private final UUID constantId;
    private final String speciesCode;
    private final EquilibriumConstantType type;
    private final int stepNumber;
    private final BigDecimal value; // K > 0
    private final BigDecimal pValue; // pK = -log10(K)
    private final EquilibriumReferenceConditions conditions;
    private final AcidBaseProvenance provenance;

    public EquilibriumConstant(
            UUID constantId,
            String speciesCode,
            EquilibriumConstantType type,
            int stepNumber,
            BigDecimal value,
            EquilibriumReferenceConditions conditions,
            AcidBaseProvenance provenance) {
        this.constantId = constantId != null ? constantId : UUID.randomUUID();
        if (speciesCode == null || speciesCode.isBlank()) {
            throw new AcidBaseException(AcidBaseErrorCode.SPECIES_NOT_FOUND, "Species code must not be null or blank");
        }
        this.speciesCode = speciesCode.trim();
        this.type = Objects.requireNonNull(type, "EquilibriumConstantType must not be null");
        if (stepNumber < 1) {
            throw new AcidBaseException(AcidBaseErrorCode.ORDERED_STEP_MISMATCH, "Step number must be at least 1: " + stepNumber);
        }
        this.stepNumber = stepNumber;
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AcidBaseException(AcidBaseErrorCode.INVALID_EQUILIBRIUM_CONSTANT, "Equilibrium constant K must be strictly positive (> 0): " + value);
        }
        this.value = value;
        // Derive pK through the shared acid-base decimal transcendental utility.
        this.pValue = AcidBaseDecimalMath.log10(value).negate().setScale(4, RoundingMode.HALF_UP);

        this.conditions = Objects.requireNonNull(conditions, "EquilibriumReferenceConditions must not be null");
        this.provenance = provenance != null ? provenance : AcidBaseProvenance.defaultExperimental();
    }

    public static EquilibriumConstant weak(String speciesCode, EquilibriumConstantType type, int stepNumber, BigDecimal value, EquilibriumReferenceConditions conditions) {
        return new EquilibriumConstant(UUID.randomUUID(), speciesCode, type, stepNumber, value, conditions, AcidBaseProvenance.defaultExperimental());
    }

    public UUID getConstantId() {
        return constantId;
    }

    public String getSpeciesCode() {
        return speciesCode;
    }

    public EquilibriumConstantType getType() {
        return type;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public BigDecimal getValue() {
        return value;
    }

    public BigDecimal getPValue() {
        return pValue;
    }

    public EquilibriumReferenceConditions getConditions() {
        return conditions;
    }

    public AcidBaseProvenance getProvenance() {
        return provenance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquilibriumConstant that = (EquilibriumConstant) o;
        return stepNumber == that.stepNumber &&
                speciesCode.equalsIgnoreCase(that.speciesCode) &&
                type == that.type &&
                conditions.equals(that.conditions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(speciesCode.toUpperCase(), type, stepNumber, conditions);
    }

    @Override
    public String toString() {
        String typeStr = type.name() + (stepNumber > 1 ? stepNumber : "");
        return typeStr + "(" + speciesCode + ") = " + value + " (p=" + pValue + ")";
    }
}
