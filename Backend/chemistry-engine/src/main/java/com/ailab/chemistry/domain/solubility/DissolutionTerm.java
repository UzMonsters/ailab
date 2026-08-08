package com.ailab.chemistry.domain.solubility;

public record DissolutionTerm(
        String speciesCode,
        String formula,
        int charge,
        int coefficient
) {
    public DissolutionTerm {
        if (speciesCode == null || speciesCode.isBlank() || formula == null || formula.isBlank()) {
            throw new SolubilityException(SolubilityErrorCode.INVALID_ION_SPECIES, "Dissolution species code and formula are required");
        }
        if (coefficient <= 0) {
            throw new SolubilityException(SolubilityErrorCode.INVALID_ION_SPECIES, "Dissolution coefficient must be positive");
        }
        speciesCode = speciesCode.trim().toUpperCase();
        formula = formula.trim();
    }
}
