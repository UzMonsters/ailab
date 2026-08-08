package com.ailab.chemistry.domain.physicalproperty;

import com.ailab.chemistry.domain.measurement.Pressure;

public final class VaporPressureDatum {
    private final Pressure vaporPressure;
    private final PropertyReferenceConditions conditions;
    private final ScientificEvidenceStatus evidenceStatus;
    private final ScientificProvenance provenance;

    public VaporPressureDatum(Pressure vaporPressure, PropertyReferenceConditions conditions, ScientificEvidenceStatus evidenceStatus, ScientificProvenance provenance) {
        if (vaporPressure == null) {
            throw new CompoundPhysicalPropertyException(CompoundPhysicalPropertyErrorCode.INVALID_VAPOR_PRESSURE_DATUM, "Vapor pressure cannot be null");
        }
        this.vaporPressure = vaporPressure;
        this.conditions = conditions != null ? conditions : PropertyReferenceConditions.stp(null);
        this.evidenceStatus = evidenceStatus != null ? evidenceStatus : ScientificEvidenceStatus.EVALUATED;
        this.provenance = provenance != null ? provenance : ScientificProvenance.crcHandbook104th("Vapor pressure datum");
    }

    public Pressure getVaporPressure() { return vaporPressure; }
    public PropertyReferenceConditions getConditions() { return conditions; }
    public ScientificEvidenceStatus getEvidenceStatus() { return evidenceStatus; }
    public ScientificProvenance getProvenance() { return provenance; }
}
