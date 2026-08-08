package com.ailab.chemistry.domain.reaction;

import java.util.Objects;
import java.util.UUID;

public final class ReactionId {
    private final UUID value;

    public ReactionId(UUID value) {
        this.value = Objects.requireNonNull(value, "ReactionId UUID must not be null");
    }

    public static ReactionId generate() {
        return new ReactionId(UUID.randomUUID());
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReactionId that = (ReactionId) o;
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
