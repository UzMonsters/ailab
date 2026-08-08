package com.ailab.chemistry.domain.element;

import java.util.Objects;
import java.util.UUID;

public final class ElementId {
    private final UUID value;

    public ElementId(UUID value) {
        this.value = Objects.requireNonNull(value, "value must not be null");
    }

    public static ElementId generate() {
        return new ElementId(UUID.randomUUID());
    }

    public static ElementId fromString(String str) {
        return new ElementId(UUID.fromString(str));
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElementId elementId = (ElementId) o;
        return value.equals(elementId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
