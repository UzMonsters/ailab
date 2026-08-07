package com.ailab.chemistry.domain.compound;

import java.util.Objects;

public final class CompoundAlias {
    private final String name;
    private final CompoundAliasRole role;

    public CompoundAlias(String name, CompoundAliasRole role) {
        if (name == null || name.isBlank()) {
            throw new CompoundException(CompoundErrorCode.INVALID_COMPOUND_NAME, "Compound alias name cannot be blank");
        }
        if (role == null) {
            throw new CompoundException(CompoundErrorCode.INVALID_COMPOUND_NAME, "Compound alias role cannot be null");
        }
        this.name = name.trim();
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public CompoundAliasRole getRole() {
        return role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompoundAlias that = (CompoundAlias) o;
        return name.equalsIgnoreCase(that.name) && role == that.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase(), role);
    }

    @Override
    public String toString() {
        return name + " (" + role + ")";
    }
}
