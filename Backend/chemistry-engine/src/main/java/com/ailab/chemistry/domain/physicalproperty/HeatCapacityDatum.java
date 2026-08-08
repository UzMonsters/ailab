package com.ailab.chemistry.domain.physicalproperty;

import com.ailab.chemistry.domain.measurement.MolarHeatCapacity;
import com.ailab.chemistry.domain.measurement.SpecificHeatCapacity;

public final class HeatCapacityDatum {

    public enum HeatCapacityBasisType {
        MOLAR, MASS_SPECIFIC
    }

    public enum ThermodynamicConditionBasis {
        CONSTANT_PRESSURE, CONSTANT_VOLUME, UNSPECIFIED
    }

    private final HeatCapacityBasisType basisType;
    private final MolarHeatCapacity molarHeatCapacity;
    private final SpecificHeatCapacity specificHeatCapacity;
    private final ThermodynamicConditionBasis conditionBasis;
    private final PropertyReferenceConditions conditions;
    private final ScientificEvidenceStatus evidenceStatus;
    private final ScientificProvenance provenance;

    public HeatCapacityDatum(MolarHeatCapacity molarHeatCapacity, ThermodynamicConditionBasis conditionBasis, PropertyReferenceConditions conditions, ScientificEvidenceStatus evidenceStatus, ScientificProvenance provenance) {
        if (molarHeatCapacity == null) {
            throw new CompoundPhysicalPropertyException(CompoundPhysicalPropertyErrorCode.INVALID_HEAT_CAPACITY_DATUM, "Molar heat capacity value cannot be null");
        }
        this.basisType = HeatCapacityBasisType.MOLAR;
        this.molarHeatCapacity = molarHeatCapacity;
        this.specificHeatCapacity = null;
        this.conditionBasis = conditionBasis != null ? conditionBasis : ThermodynamicConditionBasis.CONSTANT_PRESSURE;
        this.conditions = conditions != null ? conditions : PropertyReferenceConditions.stp(null);
        this.evidenceStatus = evidenceStatus != null ? evidenceStatus : ScientificEvidenceStatus.EVALUATED;
        this.provenance = provenance != null ? provenance : ScientificProvenance.crcHandbook104th("Heat capacity datum");
    }

    public HeatCapacityDatum(SpecificHeatCapacity specificHeatCapacity, ThermodynamicConditionBasis conditionBasis, PropertyReferenceConditions conditions, ScientificEvidenceStatus evidenceStatus, ScientificProvenance provenance) {
        if (specificHeatCapacity == null) {
            throw new CompoundPhysicalPropertyException(CompoundPhysicalPropertyErrorCode.INVALID_HEAT_CAPACITY_DATUM, "Specific heat capacity value cannot be null");
        }
        this.basisType = HeatCapacityBasisType.MASS_SPECIFIC;
        this.molarHeatCapacity = null;
        this.specificHeatCapacity = specificHeatCapacity;
        this.conditionBasis = conditionBasis != null ? conditionBasis : ThermodynamicConditionBasis.CONSTANT_PRESSURE;
        this.conditions = conditions != null ? conditions : PropertyReferenceConditions.stp(null);
        this.evidenceStatus = evidenceStatus != null ? evidenceStatus : ScientificEvidenceStatus.EVALUATED;
        this.provenance = provenance != null ? provenance : ScientificProvenance.crcHandbook104th("Heat capacity datum");
    }

    public HeatCapacityBasisType getBasisType() { return basisType; }
    public MolarHeatCapacity getMolarHeatCapacity() { return molarHeatCapacity; }
    public SpecificHeatCapacity getSpecificHeatCapacity() { return specificHeatCapacity; }
    public ThermodynamicConditionBasis getConditionBasis() { return conditionBasis; }
    public PropertyReferenceConditions getConditions() { return conditions; }
    public ScientificEvidenceStatus getEvidenceStatus() { return evidenceStatus; }
    public ScientificProvenance getProvenance() { return provenance; }
}
