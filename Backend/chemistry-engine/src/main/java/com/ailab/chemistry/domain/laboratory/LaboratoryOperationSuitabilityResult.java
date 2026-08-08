package com.ailab.chemistry.domain.laboratory;

import java.util.List;

public record LaboratoryOperationSuitabilityResult(
        LaboratoryOperationStatus status,
        List<String> selectedEquipmentProfileIds,
        String selectedContainerProfileId,
        List<LaboratoryOperationViolation> violations,
        List<LaboratoryOperationWarning> warnings,
        List<String> assumptions,
        List<String> provenance
) {
    public LaboratoryOperationSuitabilityResult {
        selectedEquipmentProfileIds = selectedEquipmentProfileIds == null ? List.of() : List.copyOf(selectedEquipmentProfileIds);
        violations = violations == null ? List.of() : List.copyOf(violations);
        warnings = warnings == null ? List.of() : List.copyOf(warnings);
        assumptions = assumptions == null ? List.of() : List.copyOf(assumptions);
        provenance = provenance == null ? List.of() : List.copyOf(provenance);
    }
}
