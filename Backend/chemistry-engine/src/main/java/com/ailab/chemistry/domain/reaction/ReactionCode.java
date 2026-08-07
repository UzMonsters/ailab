package com.ailab.chemistry.domain.reaction;

import java.util.Objects;

public final class ReactionCode {
    private final String value;

    public ReactionCode(String value) {
        Objects.requireNonNull(value, "ReactionCode must not be null");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new ReactionException(ReactionErrorCode.INVALID_REACTION_CODE, "Reaction code must not be empty or blank");
        }
        this.value = trimmed;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReactionCode that = (ReactionCode) o;
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
