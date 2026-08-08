package com.ailab.chemistry.domain.solubility;

public record SolubilityDatasetVersion(String value) {
    public SolubilityDatasetVersion {
        if (value == null || value.isBlank()) {
            throw new SolubilityException(SolubilityErrorCode.INVALID_EQUILIBRIUM_CODE, "Dataset version must not be blank");
        }
        value = value.trim();
    }
}
