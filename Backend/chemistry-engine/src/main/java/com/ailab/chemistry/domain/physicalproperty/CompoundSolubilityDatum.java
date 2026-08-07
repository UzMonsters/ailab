package com.ailab.chemistry.domain.physicalproperty;

import com.ailab.chemistry.domain.compound.CompoundId;

import java.math.BigDecimal;

public final class CompoundSolubilityDatum {

    public enum SolubilityBehavior {
        MISCIBLE, FREELY_SOLUBLE, SOLUBLE, SPARINGLY_SOLUBLE, SLIGHTLY_SOLUBLE,
        VERY_SLIGHTLY_SOLUBLE, PRACTICALLY_INSOLUBLE, IMMISCIBLE, REACTS_WITH_SOLVENT,
        DECOMPOSES_IN_SOLVENT, QUALITATIVE_ONLY, UNKNOWN
    }

    public enum SolubilityBasis {
        MASS_CONCENTRATION, MOLAR_CONCENTRATION, MASS_PERCENT, VOLUME_PERCENT,
        GRAM_PER_100_MILLILITER, GRAM_PER_100_GRAM_SOLVENT, QUALITATIVE
    }

    private final CompoundId solventId;
    private final SolubilityBehavior behavior;
    private final BigDecimal quantitativeValue;
    private final SolubilityBasis basis;
    private final String unitSymbol;
    private final PropertyReferenceConditions conditions;
    private final ScientificEvidenceStatus evidenceStatus;
    private final ScientificProvenance provenance;

    public CompoundSolubilityDatum(CompoundId solventId, SolubilityBehavior behavior, BigDecimal quantitativeValue, SolubilityBasis basis, String unitSymbol, PropertyReferenceConditions conditions, ScientificEvidenceStatus evidenceStatus, ScientificProvenance provenance) {
        if (solventId == null) {
            throw new CompoundPhysicalPropertyException(CompoundPhysicalPropertyErrorCode.INVALID_SOLUBILITY_DATUM, "Solvent compound ID cannot be null");
        }
        if (behavior == null) {
            throw new CompoundPhysicalPropertyException(CompoundPhysicalPropertyErrorCode.INVALID_SOLUBILITY_DATUM, "Solubility behavior cannot be null");
        }
        if (quantitativeValue != null && basis == null) {
            throw new CompoundPhysicalPropertyException(CompoundPhysicalPropertyErrorCode.INVALID_SOLUBILITY_DATUM, "Quantitative solubility value requires a basis");
        }
        this.solventId = solventId;
        this.behavior = behavior;
        this.quantitativeValue = quantitativeValue;
        this.basis = basis != null ? basis : SolubilityBasis.QUALITATIVE;
        this.unitSymbol = unitSymbol;
        this.conditions = conditions != null ? conditions : PropertyReferenceConditions.stp(null);
        this.evidenceStatus = evidenceStatus != null ? evidenceStatus : ScientificEvidenceStatus.EVALUATED;
        this.provenance = provenance != null ? provenance : ScientificProvenance.crcHandbook104th("Solubility datum");
    }

    public CompoundId getSolventId() { return solventId; }
    public SolubilityBehavior getBehavior() { return behavior; }
    public BigDecimal getQuantitativeValue() { return quantitativeValue; }
    public SolubilityBasis getBasis() { return basis; }
    public String getUnitSymbol() { return unitSymbol; }
    public PropertyReferenceConditions getConditions() { return conditions; }
    public ScientificEvidenceStatus getEvidenceStatus() { return evidenceStatus; }
    public ScientificProvenance getProvenance() { return provenance; }
}
