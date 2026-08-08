package com.ailab.chemistry.domain.laboratorysafety;

import java.util.Objects;

public record LaboratorySafetyRule(
        LaboratorySafetyRuleId ruleId,
        LaboratorySafetyRuleVersion version,
        LaboratorySafetyRuleType ruleType,
        LaboratorySafetySeverity severity,
        SafetyRuleApplicability applicability,
        SafetyRuleCondition condition,
        SafetyRuleProvenance provenance,
        boolean active
) {
    public LaboratorySafetyRule {
        Objects.requireNonNull(ruleId, "ruleId must not be null");
        Objects.requireNonNull(version, "version must not be null");
        Objects.requireNonNull(ruleType, "ruleType must not be null");
        Objects.requireNonNull(severity, "severity must not be null");
        Objects.requireNonNull(applicability, "applicability must not be null");
        Objects.requireNonNull(condition, "condition must not be null");
        Objects.requireNonNull(provenance, "provenance must not be null");
    }
}
