package com.ailab.chemistry.domain.laboratoryevent;

import java.util.List;

public record StepDefinitionSnapshot(String stepId, boolean optional, List<String> dependencies) {
    public StepDefinitionSnapshot {
        if (stepId == null || stepId.isBlank()) {
            throw new IllegalArgumentException("Step snapshot id is required");
        }
        dependencies = List.copyOf(dependencies == null ? List.of() : dependencies);
    }
}
