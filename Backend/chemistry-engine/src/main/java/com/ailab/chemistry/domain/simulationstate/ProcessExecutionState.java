package com.ailab.chemistry.domain.simulationstate;

import java.util.LinkedHashMap;
import java.util.Map;

public record ProcessExecutionState(String processCode, int processVersion, Map<String, ProcessStepExecution> steps) {
    public ProcessExecutionState {
        if (processCode == null) {
            processCode = "";
        }
        steps = Map.copyOf(steps == null ? Map.of() : steps);
    }

    public static ProcessExecutionState none() {
        return new ProcessExecutionState("", 0, Map.of());
    }

    public ProcessStepExecution step(String stepId) {
        ProcessStepExecution step = steps.get(stepId);
        if (step == null) {
            throw new SimulationStateException(SimulationStateErrorCode.STEP_DEPENDENCY_INCOMPLETE,
                    "Unknown process step: " + stepId);
        }
        return step;
    }

    public ProcessExecutionState withStep(ProcessStepExecution step) {
        Map<String, ProcessStepExecution> next = new LinkedHashMap<>(steps);
        next.put(step.stepId(), step);
        return new ProcessExecutionState(processCode, processVersion, next);
    }

    public ProcessExecutionState refreshAvailability() {
        Map<String, ProcessStepExecution> next = new LinkedHashMap<>(steps);
        for (ProcessStepExecution step : steps.values()) {
            if (step.status() == ProcessStepExecutionStatus.PENDING && dependenciesComplete(step, next)) {
                next.put(step.stepId(), step.withStatus(ProcessStepExecutionStatus.AVAILABLE));
            }
        }
        return new ProcessExecutionState(processCode, processVersion, next);
    }

    private boolean dependenciesComplete(ProcessStepExecution step, Map<String, ProcessStepExecution> candidates) {
        return step.dependencies().stream()
                .map(candidates::get)
                .allMatch(dep -> dep != null && (dep.status() == ProcessStepExecutionStatus.COMPLETED
                        || dep.status() == ProcessStepExecutionStatus.SKIPPED));
    }
}
