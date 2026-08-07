package com.ailab.chemistry.domain.element;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

public final class AtomicMass {
    private final BigDecimal representativeValue;
    private final AtomicMassKind kind;
    private final BigDecimal lowerBound; // nullable
    private final BigDecimal upperBound; // nullable

    public AtomicMass(BigDecimal representativeValue, AtomicMassKind kind, BigDecimal lowerBound, BigDecimal upperBound) {
        this.representativeValue = Objects.requireNonNull(representativeValue, "representativeValue must not be null");
        if (representativeValue.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Atomic mass must be positive");
        }
        this.kind = Objects.requireNonNull(kind, "kind must not be null");
        if (lowerBound != null && upperBound != null) {
            if (lowerBound.compareTo(upperBound) > 0) {
                throw new IllegalArgumentException("Lower bound must not exceed upper bound");
            }
        }
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public BigDecimal getRepresentativeValue() { return representativeValue; }
    public AtomicMassKind getKind() { return kind; }
    public Optional<BigDecimal> getLowerBound() { return Optional.ofNullable(lowerBound); }
    public Optional<BigDecimal> getUpperBound() { return Optional.ofNullable(upperBound); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AtomicMass that = (AtomicMass) o;
        return representativeValue.compareTo(that.representativeValue) == 0 &&
               kind == that.kind &&
               Objects.equals(lowerBound, that.lowerBound) &&
               Objects.equals(upperBound, that.upperBound);
    }

    @Override
    public int hashCode() {
        return Objects.hash(representativeValue, kind, lowerBound, upperBound);
    }
}
