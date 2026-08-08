package com.ailab.chemistry.domain.element.property;

import java.util.Objects;

public final class Valency implements Comparable<Valency> {
    private final int valency;
    private final boolean isCommon;
    private final ScientificEvidenceStatus evidenceStatus;
    private final PropertyProvenance provenance;

    public Valency(int valency, boolean isCommon, ScientificEvidenceStatus evidenceStatus, PropertyProvenance provenance) {
        if (valency < 0) {
            throw new ElementPropertyException(
                    ElementPropertyErrorCode.INVALID_VALENCY,
                    "Valency must be non-negative: " + valency
            );
        }
        this.valency = valency;
        this.isCommon = isCommon;
        this.evidenceStatus = evidenceStatus != null ? evidenceStatus : ScientificEvidenceStatus.EVALUATED;
        this.provenance = Objects.requireNonNull(provenance, "Provenance must not be null");
    }

    public int getValency() { return valency; }
    public boolean isCommon() { return isCommon; }
    public ScientificEvidenceStatus getEvidenceStatus() { return evidenceStatus; }
    public PropertyProvenance getProvenance() { return provenance; }

    @Override
    public int compareTo(Valency other) {
        return Integer.compare(this.valency, other.valency);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Valency valency1 = (Valency) o;
        return valency == valency1.valency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(valency);
    }

    @Override
    public String toString() {
        return String.valueOf(valency);
    }
}
