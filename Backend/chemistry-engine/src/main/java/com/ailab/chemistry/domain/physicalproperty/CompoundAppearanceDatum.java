package com.ailab.chemistry.domain.physicalproperty;

public final class CompoundAppearanceDatum {
    private final String color;
    private final String description;
    private final PropertyReferenceConditions conditions;
    private final ScientificEvidenceStatus evidenceStatus;
    private final ScientificProvenance provenance;

    public CompoundAppearanceDatum(String color, String description, PropertyReferenceConditions conditions, ScientificEvidenceStatus evidenceStatus, ScientificProvenance provenance) {
        if (description == null || description.isBlank()) {
            throw new CompoundPhysicalPropertyException(CompoundPhysicalPropertyErrorCode.INVALID_APPEARANCE_DATUM, "Appearance description cannot be blank");
        }
        this.color = color != null ? color.trim() : "Colorless";
        this.description = description.trim();
        this.conditions = conditions != null ? conditions : PropertyReferenceConditions.stp(null);
        this.evidenceStatus = evidenceStatus != null ? evidenceStatus : ScientificEvidenceStatus.EVALUATED;
        this.provenance = provenance != null ? provenance : ScientificProvenance.crcHandbook104th("Appearance datum");
    }

    public String getColor() { return color; }
    public String getDescription() { return description; }
    public PropertyReferenceConditions getConditions() { return conditions; }
    public ScientificEvidenceStatus getEvidenceStatus() { return evidenceStatus; }
    public ScientificProvenance getProvenance() { return provenance; }
}
