package com.ailab.chemistry.domain.reaction;

import java.util.Objects;

public final class ReactionTypeAssignment {
    private final ReactionTypeCode typeCode;
    private final DerivationBasis derivationBasis;
    private final String explanation;

    public ReactionTypeAssignment(ReactionTypeCode typeCode, DerivationBasis derivationBasis, String explanation) {
        Objects.requireNonNull(typeCode, "Reaction type code must not be null");
        Objects.requireNonNull(derivationBasis, "Derivation basis must not be null");

        this.typeCode = typeCode;
        this.derivationBasis = derivationBasis;
        this.explanation = explanation != null ? explanation.trim() : "";
    }

    public ReactionTypeCode getTypeCode() {
        return typeCode;
    }

    public DerivationBasis getDerivationBasis() {
        return derivationBasis;
    }

    public String getExplanation() {
        return explanation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReactionTypeAssignment that = (ReactionTypeAssignment) o;
        return typeCode == that.typeCode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeCode);
    }
}
