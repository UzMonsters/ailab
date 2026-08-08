package com.ailab.chemistry.domain.solubility;

public record SolubilityProvenance(
        String sourceIdentifier,
        String citation,
        String reuseLimitations
) {
    public SolubilityProvenance {
        if (sourceIdentifier == null || sourceIdentifier.isBlank() || citation == null || citation.isBlank()) {
            throw new SolubilityException(SolubilityErrorCode.INVALID_EQUILIBRIUM_CODE, "Source identifier and citation are required");
        }
        sourceIdentifier = sourceIdentifier.trim();
        citation = citation.trim();
        reuseLimitations = reuseLimitations == null ? "" : reuseLimitations.trim();
    }
}
