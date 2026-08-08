package com.ailab.chemistry.domain.laboratorysafety;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public record LaboratorySafetyEvaluationResult(
        LaboratorySafetyStatus status,
        SafetyEvaluationStage stage,
        List<LaboratorySafetyViolation> violations,
        List<LaboratorySafetyWarning> warnings,
        Set<String> evaluatedRuleVersions
) {
    public LaboratorySafetyEvaluationResult {
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(stage, "stage must not be null");
        violations = violations == null ? List.of() : List.copyOf(violations);
        warnings = warnings == null ? List.of() : List.copyOf(warnings);
        evaluatedRuleVersions = evaluatedRuleVersions == null ? Set.of() : Set.copyOf(evaluatedRuleVersions);
    }

    public boolean isAllowed() {
        return status == LaboratorySafetyStatus.ALLOWED || status == LaboratorySafetyStatus.ALLOWED_WITH_WARNINGS;
    }
}
