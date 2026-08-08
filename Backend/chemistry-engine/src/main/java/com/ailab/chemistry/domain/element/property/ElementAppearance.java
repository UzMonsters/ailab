package com.ailab.chemistry.domain.element.property;

import java.util.Objects;

public final class ElementAppearance {
    private final String normalizedColorName;
    private final String appearanceDescription;
    private final ScientificEvidenceStatus evidenceStatus;
    private final PropertyProvenance provenance;

    public ElementAppearance(
            String normalizedColorName,
            String appearanceDescription,
            ScientificEvidenceStatus evidenceStatus,
            PropertyProvenance provenance) {
        if (appearanceDescription != null && appearanceDescription.trim().isEmpty()) {
            throw new ElementPropertyException(
                    ElementPropertyErrorCode.INVALID_EVIDENCE_STATUS,
                    "Appearance description cannot be blank when present"
            );
        }
        this.normalizedColorName = normalizedColorName;
        this.appearanceDescription = appearanceDescription;
        this.evidenceStatus = evidenceStatus != null ? evidenceStatus : ScientificEvidenceStatus.EVALUATED;
        this.provenance = Objects.requireNonNull(provenance, "Provenance must not be null");
    }

    public String getNormalizedColorName() { return normalizedColorName; }
    public String getAppearanceDescription() { return appearanceDescription; }
    public ScientificEvidenceStatus getEvidenceStatus() { return evidenceStatus; }
    public PropertyProvenance getProvenance() { return provenance; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElementAppearance that = (ElementAppearance) o;
        return Objects.equals(normalizedColorName, that.normalizedColorName) &&
                Objects.equals(appearanceDescription, that.appearanceDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(normalizedColorName, appearanceDescription);
    }
}
