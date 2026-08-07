package com.ailab.chemistry.domain.classification;

import java.util.Objects;

public final class ClassificationCode {
    private final String value;

    public ClassificationCode(String value) {
        if (value == null || value.isBlank()) {
            throw new ClassificationException(ClassificationErrorCode.INVALID_CLASSIFICATION_CODE, "Classification code cannot be blank");
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
        ClassificationCode that = (ClassificationCode) o;
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
