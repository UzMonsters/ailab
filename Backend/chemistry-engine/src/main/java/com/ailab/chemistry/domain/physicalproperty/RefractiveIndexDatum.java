package com.ailab.chemistry.domain.physicalproperty;

import com.ailab.chemistry.domain.measurement.RefractiveIndex;

public final class RefractiveIndexDatum {
    private final RefractiveIndex refractiveIndex;
    private final PropertyReferenceConditions conditions;
    private final ScientificEvidenceStatus evidenceStatus;
    private final ScientificProvenance provenance;

    public RefractiveIndexDatum(RefractiveIndex refractiveIndex, PropertyReferenceConditions conditions, ScientificEvidenceStatus evidenceStatus, ScientificProvenance provenance) {
        if (refractiveIndex == null) {
            throw new CompoundPhysicalPropertyException(CompoundPhysicalPropertyErrorCode.INVALID_REFRACTIVE_INDEX_DATUM, "Refractive index cannot be null");
        }
        this.refractiveIndex = refractiveIndex;
        this.conditions = conditions != null ? conditions : PropertyReferenceConditions.stp(null);
        this.evidenceStatus = evidenceStatus != null ? evidenceStatus : ScientificEvidenceStatus.EVALUATED;
        this.provenance = provenance != null ? provenance : ScientificProvenance.crcHandbook104th("Refractive index datum");
    }

    public RefractiveIndex getRefractiveIndex() { return refractiveIndex; }
    public PropertyReferenceConditions getConditions() { return conditions; }
    public ScientificEvidenceStatus getEvidenceStatus() { return evidenceStatus; }
    public ScientificProvenance getProvenance() { return provenance; }
}
