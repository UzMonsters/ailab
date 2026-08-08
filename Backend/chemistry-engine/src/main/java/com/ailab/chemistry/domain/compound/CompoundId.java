package com.ailab.chemistry.domain.compound;

import java.util.Objects;
import java.util.UUID;

public final class CompoundId {
    private final UUID value;

    public CompoundId(UUID value) {
        if (value == null) {
            throw new CompoundException(CompoundErrorCode.INVALID_COMPOUND_CODE, "CompoundId value cannot be null");
        }
        this.value = value;
    }

    public static CompoundId generate() {
        return new CompoundId(UUID.randomUUID());
    }

    public static CompoundId of(String uuidStr) {
        if (uuidStr == null || uuidStr.isBlank()) {
            throw new CompoundException(CompoundErrorCode.INVALID_COMPOUND_CODE, "CompoundId string cannot be blank");
        }
        return new CompoundId(UUID.fromString(uuidStr));
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompoundId that = (CompoundId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
