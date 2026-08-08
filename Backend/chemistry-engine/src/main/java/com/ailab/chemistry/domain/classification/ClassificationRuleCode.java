package com.ailab.chemistry.domain.classification;

public enum ClassificationRuleCode {
    RULE_ELEMENTAL_COMPOSITION("RULE-ELEMENTAL-COMPOSITION", "Classifies single-element composition as MONOATOMIC_OR_ELEMENTAL and ELEMENTAL_SUBSTANCE"),
    RULE_DISTINCT_ELEMENT_COUNT("RULE-DISTINCT-ELEMENT-COUNT", "Classifies composition element count as BINARY, TERNARY, or QUATERNARY_OR_HIGHER"),
    RULE_HYDRATE_PRESENCE("RULE-HYDRATE-PRESENCE", "Classifies compounds with hydrate notation as HYDRATE"),
    RULE_NET_CHARGE("RULE-NET-CHARGE", "Classifies net charge = 0 as NEUTRAL_SPECIES and non-zero as CHARGED_SPECIES");

    private final String code;
    private final String description;

    ClassificationRuleCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
