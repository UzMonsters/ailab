package com.ailab.chemistry.domain.compound;

import java.util.Objects;

public final class CompoundExternalIdentifier {
    private final ExternalIdentifierScheme scheme;
    private final String value;

    public CompoundExternalIdentifier(ExternalIdentifierScheme scheme, String value) {
        if (scheme == null) {
            throw new CompoundException(CompoundErrorCode.INVALID_COMPOUND_CODE, "ExternalIdentifierScheme cannot be null");
        }
        if (value == null || value.isBlank()) {
            throw new CompoundException(CompoundErrorCode.INVALID_COMPOUND_CODE, "External identifier value cannot be blank");
        }
        this.scheme = scheme;
        this.value = value.trim();
    }

    public ExternalIdentifierScheme getScheme() {
        return scheme;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompoundExternalIdentifier that = (CompoundExternalIdentifier) o;
        return scheme == that.scheme && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scheme, value);
    }

    @Override
    public String toString() {
        return scheme + ":" + value;
    }
}
