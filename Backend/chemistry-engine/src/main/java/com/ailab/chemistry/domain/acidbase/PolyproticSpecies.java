package com.ailab.chemistry.domain.acidbase;

public record PolyproticSpecies(
        String speciesCode,
        String formula,
        int protonsRemaining,
        int charge
) {
    public PolyproticSpecies {
        speciesCode = requireText(speciesCode, "speciesCode");
        formula = requireText(formula, "formula");
        if (protonsRemaining < 0) {
            throw new PolyproticException(PolyproticErrorCode.INVALID_INITIAL_FORM, "Protons remaining must not be negative");
        }
    }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new PolyproticException(PolyproticErrorCode.INVALID_INITIAL_FORM, name + " must not be blank");
        }
        return value.trim();
    }
}
