package com.ailab.chemistry.domain.physicalproperty;

import com.ailab.chemistry.domain.compound.CompoundId;
import com.ailab.chemistry.domain.measurement.PhRange;
import com.ailab.chemistry.domain.measurement.PhValue;

public final class PhObservation {
    private final PhValue value;
    private final PhRange range;
    private final CompoundId solventId;
    private final String concentration;
    private final PropertyReferenceConditions conditions;
    private final ScientificEvidenceStatus evidenceStatus;
    private final ScientificProvenance provenance;

    public PhObservation(PhValue value, PhRange range, CompoundId solventId, String concentration, PropertyReferenceConditions conditions, ScientificEvidenceStatus evidenceStatus, ScientificProvenance provenance) {
        if (value == null && range == null) {
            throw new CompoundPhysicalPropertyException(CompoundPhysicalPropertyErrorCode.INVALID_PH_OBSERVATION, "pH observation requires a value or range");
        }
        if (solventId == null) {
            throw new CompoundPhysicalPropertyException(CompoundPhysicalPropertyErrorCode.INVALID_PH_OBSERVATION, "pH observation requires a solvent compound reference");
        }
        if (concentration == null || concentration.isBlank()) {
            throw new CompoundPhysicalPropertyException(CompoundPhysicalPropertyErrorCode.INVALID_PH_OBSERVATION, "pH observation requires a concentration condition");
        }
        this.value = value;
        this.range = range;
        this.solventId = solventId;
        this.concentration = concentration.trim();
        this.conditions = conditions != null ? conditions : PropertyReferenceConditions.stp(null);
        this.evidenceStatus = evidenceStatus != null ? evidenceStatus : ScientificEvidenceStatus.MEASURED;
        this.provenance = provenance != null ? provenance : ScientificProvenance.crcHandbook104th("pH observation");
    }

    public PhValue getValue() { return value; }
    public PhRange getRange() { return range; }
    public CompoundId getSolventId() { return solventId; }
    public String getConcentration() { return concentration; }
    public PropertyReferenceConditions getConditions() { return conditions; }
    public ScientificEvidenceStatus getEvidenceStatus() { return evidenceStatus; }
    public ScientificProvenance getProvenance() { return provenance; }
}
