package com.ailab.chemistry.domain.compound;

import java.util.Objects;

public final class CompoundCode {
    private final String value;

    public CompoundCode(String value) {
        if (value == null || value.isBlank()) {
            throw new CompoundException(CompoundErrorCode.INVALID_COMPOUND_CODE, "CompoundCode cannot be blank");
        }
        this.value = value.trim().toUpperCase();
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompoundCode that = (CompoundCode) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
