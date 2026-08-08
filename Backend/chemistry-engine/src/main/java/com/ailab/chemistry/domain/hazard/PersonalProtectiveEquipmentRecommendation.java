package com.ailab.chemistry.domain.hazard;

import java.util.Objects;

public final class PersonalProtectiveEquipmentRecommendation {
    private final PpeType ppeType;
    private final ProtectionLevel protectionLevel;
    private final HazardScope scope;
    private final String materialOrStandardNotes;
    private final String conditionsOfUse;
    private final String sourceDocumentId;
    private final HazardEvidenceStatus evidenceStatus;

    public PersonalProtectiveEquipmentRecommendation(PpeType ppeType, ProtectionLevel protectionLevel, HazardScope scope, String materialOrStandardNotes, String conditionsOfUse, String sourceDocumentId, HazardEvidenceStatus evidenceStatus) {
        Objects.requireNonNull(ppeType, "PpeType cannot be null");
        Objects.requireNonNull(protectionLevel, "ProtectionLevel cannot be null");
        Objects.requireNonNull(sourceDocumentId, "Source document ID cannot be null");
        this.ppeType = ppeType;
        this.protectionLevel = protectionLevel;
        this.scope = scope;
        this.materialOrStandardNotes = materialOrStandardNotes;
        this.conditionsOfUse = conditionsOfUse != null ? conditionsOfUse : "Standard laboratory handling";
        this.sourceDocumentId = sourceDocumentId;
        this.evidenceStatus = evidenceStatus != null ? evidenceStatus : HazardEvidenceStatus.AUTHORITATIVE_CLASSIFICATION;
    }

    public PpeType getPpeType() { return ppeType; }
    public ProtectionLevel getProtectionLevel() { return protectionLevel; }
    public HazardScope getScope() { return scope; }
    public String getMaterialOrStandardNotes() { return materialOrStandardNotes; }
    public String getConditionsOfUse() { return conditionsOfUse; }
    public String getSourceDocumentId() { return sourceDocumentId; }
    public HazardEvidenceStatus getEvidenceStatus() { return evidenceStatus; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonalProtectiveEquipmentRecommendation that = (PersonalProtectiveEquipmentRecommendation) o;
        return ppeType == that.ppeType && protectionLevel == that.protectionLevel && sourceDocumentId.equals(that.sourceDocumentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ppeType, protectionLevel, sourceDocumentId);
    }
}
