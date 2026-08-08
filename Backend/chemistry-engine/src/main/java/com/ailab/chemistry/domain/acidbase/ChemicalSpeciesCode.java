package com.ailab.chemistry.domain.acidbase;

import java.util.Objects;

public final class ChemicalSpeciesCode {

    private final String value;

    public ChemicalSpeciesCode(String value) {
        if (value == null || value.isBlank()) {
            throw new AcidBaseException(AcidBaseErrorCode.SPECIES_NOT_FOUND, "Chemical species code must not be null or blank");
        }
        this.value = value.trim();
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChemicalSpeciesCode that = (ChemicalSpeciesCode) o;
        return value.equalsIgnoreCase(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value.toUpperCase());
    }

    @Override
    public String toString() {
        return value;
    }
}
