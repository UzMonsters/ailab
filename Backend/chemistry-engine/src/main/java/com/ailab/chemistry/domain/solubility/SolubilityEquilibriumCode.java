package com.ailab.chemistry.domain.solubility;

public record SolubilityEquilibriumCode(String value) {
    public SolubilityEquilibriumCode {
        if (value == null || value.isBlank()) {
            throw new SolubilityException(SolubilityErrorCode.INVALID_EQUILIBRIUM_CODE, "Solubility equilibrium code must not be blank");
        }
        value = value.trim().toUpperCase();
    }
}
