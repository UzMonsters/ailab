package com.ailab.chemistry.domain.classification;

import java.util.Objects;

public final class ClassificationDefinition {
    private final ClassificationCode code;
    private final ClassificationDimension dimension;
    private final String name;
    private final String description;
    private final int sortOrder;
    private final ClassificationCode parentCode;

    public ClassificationDefinition(ClassificationCode code, ClassificationDimension dimension, String name, String description, int sortOrder, ClassificationCode parentCode) {
        if (code == null) {
            throw new ClassificationException(ClassificationErrorCode.INVALID_CLASSIFICATION_CODE, "Classification code cannot be null");
        }
        if (dimension == null) {
            throw new ClassificationException(ClassificationErrorCode.INVALID_CLASSIFICATION_CODE, "Classification dimension cannot be null");
        }
        if (name == null || name.isBlank()) {
            throw new ClassificationException(ClassificationErrorCode.INVALID_CLASSIFICATION_CODE, "Classification name cannot be blank");
        }
        if (description == null || description.isBlank()) {
            throw new ClassificationException(ClassificationErrorCode.INVALID_CLASSIFICATION_CODE, "Classification description cannot be blank");
        }
        this.code = code;
        this.dimension = dimension;
        this.name = name.trim();
        this.description = description.trim();
        this.sortOrder = sortOrder;
        this.parentCode = parentCode;
    }

    public ClassificationCode getCode() { return code; }
    public ClassificationDimension getDimension() { return dimension; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getSortOrder() { return sortOrder; }
    public ClassificationCode getParentCode() { return parentCode; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassificationDefinition that = (ClassificationDefinition) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return code.getValue() + " (" + name + ")";
    }
}
