package com.ailab.chemistry.domain.simulationstate;

import java.util.List;
import java.util.Map;

public record ProcessStepExecution(
        String stepId,
        boolean optional,
        List<String> dependencies,
        ProcessStepExecutionStatus status,
        Map<String, String> explicitOutcome
) {
    public ProcessStepExecution {
        if (stepId == null || stepId.isBlank() || status == null) {
            throw new IllegalArgumentException("Step execution id and status are required");
        }
        dependencies = List.copyOf(dependencies == null ? List.of() : dependencies);
        explicitOutcome = Map.copyOf(explicitOutcome == null ? Map.of() : explicitOutcome);
    }

    public ProcessStepExecution withStatus(ProcessStepExecutionStatus status) {
        return new ProcessStepExecution(stepId, optional, dependencies, status, explicitOutcome);
    }

    public ProcessStepExecution withOutcome(ProcessStepExecutionStatus status, Map<String, String> outcome) {
        return new ProcessStepExecution(stepId, optional, dependencies, status, outcome);
    }
}
