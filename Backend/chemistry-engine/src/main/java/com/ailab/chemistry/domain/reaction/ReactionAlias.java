package com.ailab.chemistry.domain.reaction;

import java.util.Objects;

public final class ReactionAlias {
    private final String aliasName;
    private final String aliasType;

    public ReactionAlias(String aliasName, String aliasType) {
        Objects.requireNonNull(aliasName, "Alias name must not be null");
        String trimmed = aliasName.trim();
        if (trimmed.isEmpty()) {
            throw new ReactionException(ReactionErrorCode.INVALID_REACTION_NAME, "Alias name must not be blank");
        }
        this.aliasName = trimmed;
        this.aliasType = aliasType != null ? aliasType.trim() : "COMMON";
    }

    public String getAliasName() {
        return aliasName;
    }

    public String getAliasType() {
        return aliasType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReactionAlias that = (ReactionAlias) o;
        return Objects.equals(aliasName.toLowerCase(), that.aliasName.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(aliasName.toLowerCase());
    }
}
