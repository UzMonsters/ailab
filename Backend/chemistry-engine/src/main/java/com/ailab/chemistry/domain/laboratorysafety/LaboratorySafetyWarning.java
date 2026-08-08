package com.ailab.chemistry.domain.laboratorysafety;

import java.util.Objects;

public record LaboratorySafetyWarning(
        LaboratorySafetyRuleId ruleId,
        LaboratorySafetyRuleVersion version,
        LaboratorySafetyRuleType ruleType,
        String message,
        String citation
) {
    public LaboratorySafetyWarning {
        Objects.requireNonNull(ruleId, "ruleId must not be null");
        Objects.requireNonNull(version, "version must not be null");
        Objects.requireNonNull(ruleType, "ruleType must not be null");
        Objects.requireNonNull(message, "message must not be null");
        Objects.requireNonNull(citation, "citation must not be null");
    }
}
