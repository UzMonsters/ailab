package com.ailab.chemistry.domain.physicalproperty;

import com.ailab.chemistry.domain.measurement.DynamicViscosity;

public final class ViscosityDatum {
    private final DynamicViscosity viscosity;
    private final PropertyReferenceConditions conditions;
    private final ScientificEvidenceStatus evidenceStatus;
    private final ScientificProvenance provenance;

    public ViscosityDatum(DynamicViscosity viscosity, PropertyReferenceConditions conditions, ScientificEvidenceStatus evidenceStatus, ScientificProvenance provenance) {
        if (viscosity == null) {
            throw new CompoundPhysicalPropertyException(CompoundPhysicalPropertyErrorCode.INVALID_VISCOSITY_DATUM, "Dynamic viscosity cannot be null");
        }
        this.viscosity = viscosity;
        this.conditions = conditions != null ? conditions : PropertyReferenceConditions.stp(null);
        this.evidenceStatus = evidenceStatus != null ? evidenceStatus : ScientificEvidenceStatus.EVALUATED;
        this.provenance = provenance != null ? provenance : ScientificProvenance.crcHandbook104th("Viscosity datum");
    }

    public DynamicViscosity getViscosity() { return viscosity; }
    public PropertyReferenceConditions getConditions() { return conditions; }
    public ScientificEvidenceStatus getEvidenceStatus() { return evidenceStatus; }
    public ScientificProvenance getProvenance() { return provenance; }
}
