package com.ailab.chemistry.domain.laboratorysafety;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public record SafetyRuleCondition(
        String field,
        String operator,
        String targetValue,
        Map<String, String> parameters
) {
    public SafetyRuleCondition {
        Objects.requireNonNull(field, "field must not be null");
        Objects.requireNonNull(operator, "operator must not be null");
        Objects.requireNonNull(targetValue, "targetValue must not be null");
        parameters = parameters == null ? Map.of() : Map.copyOf(parameters);
    }

    public static SafetyRuleCondition equalsCondition(String field, String value) {
        return new SafetyRuleCondition(field, "EQUALS", value, Map.of());
    }

    public static SafetyRuleCondition greaterThan(String field, String value) {
        return new SafetyRuleCondition(field, "GREATER_THAN", value, Map.of());
    }

    public static SafetyRuleCondition notEquals(String field, String value) {
        return new SafetyRuleCondition(field, "NOT_EQUALS", value, Map.of());
    }
}
