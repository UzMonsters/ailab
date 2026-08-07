package com.ailab.chemistry.domain.hazard;

import java.util.Objects;

public final class HazardProvenance {
    private final String sourceId;
    private final String statement;
    private final HazardEvidenceStatus evidenceStatus;

    public HazardProvenance(String sourceId, String statement, HazardEvidenceStatus evidenceStatus) {
        Objects.requireNonNull(sourceId, "Source ID cannot be null");
        Objects.requireNonNull(evidenceStatus, "Evidence status cannot be null");
        this.sourceId = sourceId;
        this.statement = statement != null ? statement : "GHS Reference Classification";
        this.evidenceStatus = evidenceStatus;
    }

    public static HazardProvenance unGhsRev11(String statement) {
        return new HazardProvenance("UN-GHS-REV11-2025", statement, HazardEvidenceStatus.AUTHORITATIVE_CLASSIFICATION);
    }

    public String getSourceId() { return sourceId; }
    public String getStatement() { return statement; }
    public HazardEvidenceStatus getEvidenceStatus() { return evidenceStatus; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HazardProvenance that = (HazardProvenance) o;
        return sourceId.equals(that.sourceId) && evidenceStatus == that.evidenceStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceId, evidenceStatus);
    }
}
