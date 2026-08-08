package com.ailab.chemistry.domain.laboratorysafety;

import java.util.Objects;
import java.util.Set;

public record SafetyRuleApplicability(
        SafetyEvaluationStage stage,
        Set<String> operationTypes,
        Set<String> requiredInputFields
) {
    public SafetyRuleApplicability {
        Objects.requireNonNull(stage, "stage must not be null");
        operationTypes = operationTypes == null ? Set.of() : Set.copyOf(operationTypes);
        requiredInputFields = requiredInputFields == null ? Set.of() : Set.copyOf(requiredInputFields);
    }
}
