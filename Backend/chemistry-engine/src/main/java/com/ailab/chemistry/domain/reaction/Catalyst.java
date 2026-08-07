package com.ailab.chemistry.domain.reaction;

import java.util.Objects;
import java.util.UUID;

public final class Catalyst {
    private final UUID id;
    private final CatalystReferenceType referenceType;
    private final String referenceCode;
    private final CatalystRole role;
    private final String physicalForm;
    private final String loadingDescription;
    private final ReactionEvidenceStatus evidenceStatus;
    private final ReactionProvenance provenance;

    public Catalyst(UUID id, CatalystReferenceType referenceType, String referenceCode, CatalystRole role,
                    String physicalForm, String loadingDescription, ReactionEvidenceStatus evidenceStatus,
                    ReactionProvenance provenance) {
        Objects.requireNonNull(referenceType, "Catalyst reference type must not be null");
        Objects.requireNonNull(referenceCode, "Catalyst reference code must not be null");
        Objects.requireNonNull(role, "Catalyst role must not be null");

        if (referenceCode.isBlank()) {
            throw new ReactionException(ReactionErrorCode.INVALID_REACTION_CATALYST, "Catalyst reference code must not be blank");
        }

        this.id = id != null ? id : UUID.randomUUID();
        this.referenceType = referenceType;
        this.referenceCode = referenceCode.trim();
        this.role = role;
        this.physicalForm = physicalForm != null ? physicalForm.trim() : "";
        this.loadingDescription = loadingDescription != null ? loadingDescription.trim() : "";
        this.evidenceStatus = evidenceStatus != null ? evidenceStatus : ReactionEvidenceStatus.CURATED_AUTHORITATIVE;
        this.provenance = provenance;
    }

    public UUID getId() {
        return id;
    }

    public CatalystReferenceType getReferenceType() {
        return referenceType;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public CatalystRole getRole() {
        return role;
    }

    public String getPhysicalForm() {
        return physicalForm;
    }

    public String getLoadingDescription() {
        return loadingDescription;
    }

    public ReactionEvidenceStatus getEvidenceStatus() {
        return evidenceStatus;
    }

    public ReactionProvenance getProvenance() {
        return provenance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Catalyst catalyst = (Catalyst) o;
        return referenceType == catalyst.referenceType &&
                Objects.equals(referenceCode, catalyst.referenceCode) &&
                role == catalyst.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(referenceType, referenceCode, role);
    }
}
