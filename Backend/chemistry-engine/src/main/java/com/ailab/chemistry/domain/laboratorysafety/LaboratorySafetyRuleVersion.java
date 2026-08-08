package com.ailab.chemistry.domain.laboratorysafety;

public record LaboratorySafetyRuleVersion(int value) {
    public LaboratorySafetyRuleVersion {
        if (value < 1) {
            throw new IllegalArgumentException("LaboratorySafetyRuleVersion must be >= 1");
        }
    }
}
