package com.ailab.chemistry.domain.compound;

import java.util.Objects;

public final class CompoundCharge {
    private final int value;

    public CompoundCharge(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompoundCharge that = (CompoundCharge) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        if (value == 0) return "0";
        if (value > 0) return "+" + value;
        return String.valueOf(value);
    }
}
