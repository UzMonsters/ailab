package com.ailab.chemistry.domain.reaction;

import java.util.Objects;

public final class ReactionTypeDefinition {
    private final ReactionTypeCode typeCode;
    private final String name;
    private final String description;
    private final int displayOrder;

    public ReactionTypeDefinition(ReactionTypeCode typeCode, String name, String description, int displayOrder) {
        Objects.requireNonNull(typeCode, "Reaction type code must not be null");
        Objects.requireNonNull(name, "Reaction type name must not be null");

        this.typeCode = typeCode;
        this.name = name.trim();
        this.description = description != null ? description.trim() : "";
        this.displayOrder = displayOrder;
    }

    public ReactionTypeCode getTypeCode() {
        return typeCode;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReactionTypeDefinition that = (ReactionTypeDefinition) o;
        return typeCode == that.typeCode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeCode);
    }
}
