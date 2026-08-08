package com.ailab.chemistry.domain.element.property;

import java.math.BigDecimal;
import java.util.Objects;

public final class Electronegativity {
    private final BigDecimal value;
    private final ElectronegativityScale scale;
    private final boolean isPredicted;
    private final ScientificEvidenceStatus evidenceStatus;
    private final PropertyProvenance provenance;

    public Electronegativity(
            BigDecimal value,
            ElectronegativityScale scale,
            boolean isPredicted,
            ScientificEvidenceStatus evidenceStatus,
            PropertyProvenance provenance) {
        Objects.requireNonNull(value, "Electronegativity value must not be null");
        Objects.requireNonNull(scale, "Electronegativity scale must not be null");
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ElementPropertyException(
                    ElementPropertyErrorCode.INVALID_ELECTRONEGATIVITY,
                    "Electronegativity value must be positive: " + value
            );
        }
        this.value = value;
        this.scale = scale;
        this.isPredicted = isPredicted;
        this.evidenceStatus = evidenceStatus != null ? evidenceStatus : ScientificEvidenceStatus.EVALUATED;
        this.provenance = Objects.requireNonNull(provenance, "Provenance must not be null");
    }

    public BigDecimal getValue() { return value; }
    public ElectronegativityScale getScale() { return scale; }
    public boolean isPredicted() { return isPredicted; }
    public ScientificEvidenceStatus getEvidenceStatus() { return evidenceStatus; }
    public PropertyProvenance getProvenance() { return provenance; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Electronegativity that = (Electronegativity) o;
        return scale == that.scale && value.compareTo(that.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value.stripTrailingZeros(), scale);
    }

    @Override
    public String toString() {
        return value.stripTrailingZeros().toPlainString() + " (" + scale + ")";
    }
}
