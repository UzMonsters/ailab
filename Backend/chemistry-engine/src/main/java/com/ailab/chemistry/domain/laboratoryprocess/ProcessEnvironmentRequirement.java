package com.ailab.chemistry.domain.laboratoryprocess;

public record ProcessEnvironmentRequirement(String requirementId, String ventilationMode, boolean fumeHoodRequired) {
    public ProcessEnvironmentRequirement {
        if (requirementId == null || requirementId.isBlank()) {
            throw new IllegalArgumentException("Environment requirement id is required");
        }
    }
}
