package com.ailab.chemistry.domain.thermodynamics;

public record ThermodynamicProvenance(String sourceIdentifier, String citation, String reuseLimitations) {
    public ThermodynamicProvenance {
        if (blank(sourceIdentifier) || blank(citation) || blank(reuseLimitations)) {
            throw new ThermodynamicException(ThermodynamicErrorCode.INCOMPLETE_PROVENANCE,
                    "Thermodynamic records require complete source identifier, citation and reuse limitations");
        }
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
