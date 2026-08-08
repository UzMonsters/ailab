package com.ailab.chemistry.domain.physicalproperty;

public final class CompoundPolarityDatum {

    public enum PolarityClassification {
        POLAR, NONPOLAR, IONIC, AMPHIPHILIC, CONTEXT_DEPENDENT, UNKNOWN
    }

    private final PolarityClassification classification;
    private final ScientificEvidenceStatus evidenceStatus;
    private final ScientificProvenance provenance;

    public CompoundPolarityDatum(PolarityClassification classification, ScientificEvidenceStatus evidenceStatus, ScientificProvenance provenance) {
        if (classification == null) {
            throw new CompoundPhysicalPropertyException(CompoundPhysicalPropertyErrorCode.INVALID_POLARITY_DATUM, "Polarity classification cannot be null");
        }
        this.classification = classification;
        this.evidenceStatus = evidenceStatus != null ? evidenceStatus : ScientificEvidenceStatus.EVALUATED;
        this.provenance = provenance != null ? provenance : ScientificProvenance.crcHandbook104th("Polarity datum");
    }

    public PolarityClassification getClassification() { return classification; }
    public ScientificEvidenceStatus getEvidenceStatus() { return evidenceStatus; }
    public ScientificProvenance getProvenance() { return provenance; }
}
