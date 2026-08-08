package com.ailab.chemistry.domain.reaction;

import java.util.Objects;

public final class ReactionName {
    private final String value;

    public ReactionName(String value) {
        Objects.requireNonNull(value, "Reaction name must not be null");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new ReactionException(ReactionErrorCode.INVALID_REACTION_NAME, "Reaction name must not be blank");
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
        ReactionName that = (ReactionName) o;
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
