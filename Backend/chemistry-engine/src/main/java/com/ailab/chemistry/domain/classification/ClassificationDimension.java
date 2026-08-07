package com.ailab.chemistry.domain.classification;

public enum ClassificationDimension {
    SUBSTANCE_DOMAIN(true),               // Exactly 1 per profile
    COMPOSITION_PATTERN(false),           // Multiple allowed
    INORGANIC_FUNCTIONAL_CLASS(false),   // Zero or multiple
    ACID_SUBTYPE(true),                  // Zero or one
    SALT_SUBTYPE(false),                 // Zero or multiple
    ORGANIC_FUNCTIONAL_CLASS(false);     // Zero or multiple

    private final boolean singleValued;

    ClassificationDimension(boolean singleValued) {
        this.singleValued = singleValued;
    }

    public boolean isSingleValued() {
        return singleValued;
    }
}
