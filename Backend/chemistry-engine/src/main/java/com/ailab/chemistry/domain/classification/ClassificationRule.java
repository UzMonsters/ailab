package com.ailab.chemistry.domain.classification;

import java.util.List;

public final class ClassificationRule {
    private final ClassificationRuleCode code;
    private final String description;
    private final List<ClassificationCode> outputCodes;

    public ClassificationRule(ClassificationRuleCode code, String description, List<ClassificationCode> outputCodes) {
        this.code = code;
        this.description = description;
        this.outputCodes = List.copyOf(outputCodes);
    }

    public ClassificationRuleCode getCode() { return code; }
    public String getDescription() { return description; }
    public List<ClassificationCode> getOutputCodes() { return outputCodes; }
}
