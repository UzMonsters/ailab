package com.ailab.chemistry.domain.equipment;

import java.util.List;

public record EquipmentSuitabilityResult(
        EquipmentSuitabilityStatus status,
        List<String> selectedProfileIds,
        List<EquipmentViolation> violations,
        List<EquipmentWarning> warnings,
        List<String> provenance
) {
    public EquipmentSuitabilityResult {
        selectedProfileIds = selectedProfileIds == null ? List.of() : List.copyOf(selectedProfileIds);
        violations = violations == null ? List.of() : List.copyOf(violations);
        warnings = warnings == null ? List.of() : List.copyOf(warnings);
        provenance = provenance == null ? List.of() : List.copyOf(provenance);
    }

    public List<EquipmentErrorCode> errorCodes() {
        return violations.stream().map(EquipmentViolation::code).toList();
    }
}
