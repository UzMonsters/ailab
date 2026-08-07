package com.ailab.chemistry.domain.thermodynamics;

public record ThermodynamicDatasetVersion(String value) {
    public ThermodynamicDatasetVersion {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Thermodynamic dataset version is required");
        }
    }
}
