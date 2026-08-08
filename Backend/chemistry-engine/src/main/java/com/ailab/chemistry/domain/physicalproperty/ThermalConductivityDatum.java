package com.ailab.chemistry.domain.physicalproperty;

import com.ailab.chemistry.domain.measurement.ThermalConductivity;

public final class ThermalConductivityDatum {
    private final ThermalConductivity thermalConductivity;
    private final PropertyReferenceConditions conditions;
    private final ScientificEvidenceStatus evidenceStatus;
    private final ScientificProvenance provenance;

    public ThermalConductivityDatum(ThermalConductivity thermalConductivity, PropertyReferenceConditions conditions, ScientificEvidenceStatus evidenceStatus, ScientificProvenance provenance) {
        if (thermalConductivity == null) {
            throw new CompoundPhysicalPropertyException(CompoundPhysicalPropertyErrorCode.INVALID_THERMAL_CONDUCTIVITY_DATUM, "Thermal conductivity cannot be null");
        }
        this.thermalConductivity = thermalConductivity;
        this.conditions = conditions != null ? conditions : PropertyReferenceConditions.stp(null);
        this.evidenceStatus = evidenceStatus != null ? evidenceStatus : ScientificEvidenceStatus.EVALUATED;
        this.provenance = provenance != null ? provenance : ScientificProvenance.crcHandbook104th("Thermal conductivity datum");
    }

    public ThermalConductivity getThermalConductivity() { return thermalConductivity; }
    public PropertyReferenceConditions getConditions() { return conditions; }
    public ScientificEvidenceStatus getEvidenceStatus() { return evidenceStatus; }
    public ScientificProvenance getProvenance() { return provenance; }
}
