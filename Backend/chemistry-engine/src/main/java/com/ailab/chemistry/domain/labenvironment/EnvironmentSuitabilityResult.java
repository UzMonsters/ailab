package com.ailab.chemistry.domain.labenvironment;

import java.util.List;

public record EnvironmentSuitabilityResult(
        EnvironmentSuitabilityStatus status,
        List<EnvironmentViolation> violations,
        List<EnvironmentWarning> warnings,
        List<String> provenance
) {
    public EnvironmentSuitabilityResult {
        violations = violations == null ? List.of() : List.copyOf(violations);
        warnings = warnings == null ? List.of() : List.copyOf(warnings);
        provenance = provenance == null ? List.of() : List.copyOf(provenance);
    }

    public List<EnvironmentErrorCode> errorCodes() {
        return violations.stream().map(EnvironmentViolation::code).toList();
    }
}
