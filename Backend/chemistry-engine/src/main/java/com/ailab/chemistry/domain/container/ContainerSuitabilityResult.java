package com.ailab.chemistry.domain.container;

import java.util.List;

public record ContainerSuitabilityResult(
        ContainerSuitabilityStatus status,
        String selectedProfileId,
        FillFraction fillFraction,
        Headspace headspace,
        List<ContainerViolation> violations,
        List<String> provenance
) {
    public ContainerSuitabilityResult {
        violations = violations == null ? List.of() : List.copyOf(violations);
        provenance = provenance == null ? List.of() : List.copyOf(provenance);
    }

    public List<ContainerErrorCode> errorCodes() {
        return violations.stream().map(ContainerViolation::code).toList();
    }
}
