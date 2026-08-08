package com.ailab.chemistry.domain.laboratorysafety;

import java.util.Objects;

public record LaboratorySafetyRuleId(String value) {
    public LaboratorySafetyRuleId {
        Objects.requireNonNull(value, "value must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("LaboratorySafetyRuleId must not be blank");
        }
    }
}
