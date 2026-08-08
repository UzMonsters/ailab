package com.ailab.chemistry.domain.laboratorysafety;

import java.util.Objects;

public record SafetyRuleProvenance(
        SafetyRuleSourceType sourceType,
        String sourceIdentifier,
        String sourceCitation,
        String sourceVersionDate,
        String exactClauseSection,
        String supportedClaim,
        String sourceUrl,
        String evidenceStatus,
        int effectiveVersion
) {
    public SafetyRuleProvenance {
        Objects.requireNonNull(sourceType, "sourceType must not be null");
        Objects.requireNonNull(sourceIdentifier, "sourceIdentifier must not be null");
        Objects.requireNonNull(sourceCitation, "sourceCitation must not be null");
        Objects.requireNonNull(sourceVersionDate, "sourceVersionDate must not be null");
        Objects.requireNonNull(evidenceStatus, "evidenceStatus must not be null");
        exactClauseSection = exactClauseSection == null ? "" : exactClauseSection;
        supportedClaim = supportedClaim == null ? "" : supportedClaim;
        sourceUrl = sourceUrl == null ? "" : sourceUrl;
    }

    public static SafetyRuleProvenance defaultSourced(String sourceIdentifier, String sourceCitation) {
        return new SafetyRuleProvenance(SafetyRuleSourceType.INTERNAL_GOVERNED_POLICY, sourceIdentifier, sourceCitation, "2026-01-01", "", "", "", "VERIFIED", 1);
    }
}
