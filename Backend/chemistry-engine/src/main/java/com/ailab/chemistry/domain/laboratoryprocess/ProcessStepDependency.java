package com.ailab.chemistry.domain.laboratoryprocess;

public record ProcessStepDependency(ProcessStepId stepId) {
    public ProcessStepDependency {
        if (stepId == null) {
            throw new IllegalArgumentException("Dependency step id is required");
        }
    }
}
