package com.ailab.chemistry.domain.electrochemistry;

public record ElectrochemicalProvenance(
        String sourceCode,
        String citation,
        String reuseTerms,
        ElectrochemicalEvidenceStatus evidenceStatus
) {
    public ElectrochemicalProvenance {
        if (sourceCode == null || sourceCode.isBlank() || citation == null || citation.isBlank() || reuseTerms == null || reuseTerms.isBlank()) {
            throw new ElectrochemicalException(ElectrochemicalErrorCode.INVALID_HALF_REACTION, "Complete electrochemical provenance is required");
        }
    }
}
