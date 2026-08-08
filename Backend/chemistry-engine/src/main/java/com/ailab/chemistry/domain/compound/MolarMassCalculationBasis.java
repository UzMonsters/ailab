package com.ailab.chemistry.domain.compound;

import java.util.Objects;

public final class MolarMassCalculationBasis {
    private final String elementDatasetVersion;
    private final String algorithmVersion;

    public MolarMassCalculationBasis(String elementDatasetVersion, String algorithmVersion) {
        if (elementDatasetVersion == null || elementDatasetVersion.isBlank()) {
            throw new CompoundException(CompoundErrorCode.INVALID_MOLAR_MASS, "Element dataset version cannot be blank");
        }
        if (algorithmVersion == null || algorithmVersion.isBlank()) {
            throw new CompoundException(CompoundErrorCode.INVALID_MOLAR_MASS, "Algorithm version cannot be blank");
        }
        this.elementDatasetVersion = elementDatasetVersion.trim();
        this.algorithmVersion = algorithmVersion.trim();
    }

    public String getElementDatasetVersion() {
        return elementDatasetVersion;
    }

    public String getAlgorithmVersion() {
        return algorithmVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MolarMassCalculationBasis that = (MolarMassCalculationBasis) o;
        return Objects.equals(elementDatasetVersion, that.elementDatasetVersion) &&
               Objects.equals(algorithmVersion, that.algorithmVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elementDatasetVersion, algorithmVersion);
    }

    @Override
    public String toString() {
        return elementDatasetVersion + " (" + algorithmVersion + ")";
    }
}
