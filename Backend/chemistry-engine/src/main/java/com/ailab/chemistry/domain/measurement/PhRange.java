package com.ailab.chemistry.domain.measurement;

import java.util.Objects;

public final class PhRange {
    private final PhValue lowerBound;
    private final PhValue upperBound;

    public PhRange(PhValue lowerBound, PhValue upperBound) {
        if (lowerBound == null || upperBound == null) {
            throw new IllegalArgumentException("pH range bounds cannot be null");
        }
        if (lowerBound.getValue().compareTo(upperBound.getValue()) > 0) {
            throw new IllegalArgumentException("pH range lower bound must be <= upper bound: " + lowerBound + " > " + upperBound);
        }
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public PhValue getLowerBound() { return lowerBound; }
    public PhValue getUpperBound() { return upperBound; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhRange phRange = (PhRange) o;
        return Objects.equals(lowerBound, phRange.lowerBound) && Objects.equals(upperBound, phRange.upperBound);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lowerBound, upperBound);
    }

    @Override
    public String toString() {
        return lowerBound.toString() + " - " + upperBound.toString();
    }
}
