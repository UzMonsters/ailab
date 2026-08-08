package com.ailab.chemistry.domain.physicalproperty;

import com.ailab.chemistry.domain.measurement.ElectricalConductivity;

public final class ElectricalConductivityDatum {
    private final ElectricalConductivity electricalConductivity;
    private final PropertyReferenceConditions conditions;
    private final ScientificEvidenceStatus evidenceStatus;
    private final ScientificProvenance provenance;

    public ElectricalConductivityDatum(ElectricalConductivity electricalConductivity, PropertyReferenceConditions conditions, ScientificEvidenceStatus evidenceStatus, ScientificProvenance provenance) {
        if (electricalConductivity == null) {
            throw new CompoundPhysicalPropertyException(CompoundPhysicalPropertyErrorCode.INVALID_ELECTRICAL_CONDUCTIVITY_DATUM, "Electrical conductivity cannot be null");
        }
        this.electricalConductivity = electricalConductivity;
        this.conditions = conditions != null ? conditions : PropertyReferenceConditions.stp(null);
        this.evidenceStatus = evidenceStatus != null ? evidenceStatus : ScientificEvidenceStatus.EVALUATED;
        this.provenance = provenance != null ? provenance : ScientificProvenance.crcHandbook104th("Electrical conductivity datum");
    }

    public ElectricalConductivity getElectricalConductivity() { return electricalConductivity; }
    public PropertyReferenceConditions getConditions() { return conditions; }
    public ScientificEvidenceStatus getEvidenceStatus() { return evidenceStatus; }
    public ScientificProvenance getProvenance() { return provenance; }
}
