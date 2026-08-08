package com.ailab.chemistry.domain.physicalproperty;

import com.ailab.chemistry.domain.measurement.SurfaceTension;

public final class SurfaceTensionDatum {
    private final SurfaceTension surfaceTension;
    private final String interfaceDescription;
    private final PropertyReferenceConditions conditions;
    private final ScientificEvidenceStatus evidenceStatus;
    private final ScientificProvenance provenance;

    public SurfaceTensionDatum(SurfaceTension surfaceTension, String interfaceDescription, PropertyReferenceConditions conditions, ScientificEvidenceStatus evidenceStatus, ScientificProvenance provenance) {
        if (surfaceTension == null) {
            throw new CompoundPhysicalPropertyException(CompoundPhysicalPropertyErrorCode.INVALID_SURFACE_TENSION_DATUM, "Surface tension cannot be null");
        }
        this.surfaceTension = surfaceTension;
        this.interfaceDescription = interfaceDescription != null ? interfaceDescription.trim() : "liquid-air";
        this.conditions = conditions != null ? conditions : PropertyReferenceConditions.stp(null);
        this.evidenceStatus = evidenceStatus != null ? evidenceStatus : ScientificEvidenceStatus.EVALUATED;
        this.provenance = provenance != null ? provenance : ScientificProvenance.crcHandbook104th("Surface tension datum");
    }

    public SurfaceTension getSurfaceTension() { return surfaceTension; }
    public String getInterfaceDescription() { return interfaceDescription; }
    public PropertyReferenceConditions getConditions() { return conditions; }
    public ScientificEvidenceStatus getEvidenceStatus() { return evidenceStatus; }
    public ScientificProvenance getProvenance() { return provenance; }
}
