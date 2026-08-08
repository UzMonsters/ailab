package com.ailab.chemistry.domain.element.property;

import java.util.Objects;

public final class OxidationState implements Comparable<OxidationState> {
    private final int state;
    private final boolean isCommon;
    private final boolean isUncommon;
    private final boolean isPredicted;
    private final ScientificEvidenceStatus evidenceStatus;
    private final PropertyProvenance provenance;

    public OxidationState(
            int state,
            boolean isCommon,
            boolean isUncommon,
            boolean isPredicted,
            ScientificEvidenceStatus evidenceStatus,
            PropertyProvenance provenance) {
        this.state = state;
        this.isCommon = isCommon;
        this.isUncommon = isUncommon;
        this.isPredicted = isPredicted;
        this.evidenceStatus = evidenceStatus != null ? evidenceStatus : ScientificEvidenceStatus.EVALUATED;
        this.provenance = Objects.requireNonNull(provenance, "Provenance must not be null");
    }

    public int getState() { return state; }
    public boolean isCommon() { return isCommon; }
    public boolean isUncommon() { return isUncommon; }
    public boolean isPredicted() { return isPredicted; }
    public ScientificEvidenceStatus getEvidenceStatus() { return evidenceStatus; }
    public PropertyProvenance getProvenance() { return provenance; }

    @Override
    public int compareTo(OxidationState other) {
        return Integer.compare(this.state, other.state);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OxidationState that = (OxidationState) o;
        return state == that.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(state);
    }

    @Override
    public String toString() {
        return (state > 0 ? "+" : "") + state;
    }
}
