package com.ailab.chemistry.domain.acidbase;

import java.util.Objects;

public final class AcidBaseProvenance {

    private final String sourceIdentifier;
    private final String citation;
    private final String license;
    private final String evidenceStatus;

    public AcidBaseProvenance(String sourceIdentifier, String citation, String license, String evidenceStatus) {
        this.sourceIdentifier = sourceIdentifier != null ? sourceIdentifier.trim() : "IUPAC/CRC-HANDBOOK";
        this.citation = citation != null ? citation.trim() : "CRC Handbook of Chemistry and Physics, 104th Ed.";
        this.license = license != null ? license.trim() : "Public Academic / IUPAC Data Standard";
        this.evidenceStatus = evidenceStatus != null ? evidenceStatus.trim() : "PEER_REVIEWED_EXPERIMENTAL";
    }

    public static AcidBaseProvenance defaultExperimental() {
        return new AcidBaseProvenance("IUPAC/CRC-HANDBOOK", "CRC Handbook of Chemistry and Physics, 104th Ed.", "Public Academic / IUPAC Data Standard", "PEER_REVIEWED_EXPERIMENTAL");
    }

    public String getSourceIdentifier() {
        return sourceIdentifier;
    }

    public String getCitation() {
        return citation;
    }

    public String getLicense() {
        return license;
    }

    public String getEvidenceStatus() {
        return evidenceStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AcidBaseProvenance that = (AcidBaseProvenance) o;
        return Objects.equals(sourceIdentifier, that.sourceIdentifier) &&
                Objects.equals(citation, that.citation) &&
                Objects.equals(evidenceStatus, that.evidenceStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceIdentifier, citation, evidenceStatus);
    }

    @Override
    public String toString() {
        return sourceIdentifier + " [" + evidenceStatus + "]";
    }
}
