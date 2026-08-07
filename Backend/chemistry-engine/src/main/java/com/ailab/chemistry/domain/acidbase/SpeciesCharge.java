package com.ailab.chemistry.domain.acidbase;

import java.util.Objects;

public final class SpeciesCharge implements Comparable<SpeciesCharge> {

    public static final SpeciesCharge ZERO = new SpeciesCharge(0);
    public static final SpeciesCharge PLUS_ONE = new SpeciesCharge(1);
    public static final SpeciesCharge MINUS_ONE = new SpeciesCharge(-1);

    private final int value;

    public SpeciesCharge(int value) {
        this.value = value;
    }

    public static SpeciesCharge of(int value) {
        return new SpeciesCharge(value);
    }

    public int getValue() {
        return value;
    }

    public boolean isNeutral() {
        return value == 0;
    }

    public boolean isCation() {
        return value > 0;
    }

    public boolean isAnion() {
        return value < 0;
    }

    @Override
    public int compareTo(SpeciesCharge other) {
        Objects.requireNonNull(other, "Other charge must not be null");
        return Integer.compare(this.value, other.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpeciesCharge that = (SpeciesCharge) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public String toString() {
        if (value == 0) return "0";
        if (value == 1) return "+1";
        if (value == -1) return "-1";
        return (value > 0 ? "+" : "") + value;
    }
}
