package com.ailab.chemistry.domain.laboratorysafety;

import com.ailab.chemistry.domain.simulationengine.SimulationCommand;
import com.ailab.chemistry.domain.simulationengine.SimulationStateDelta;
import com.ailab.chemistry.domain.simulationstate.SimulationState;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public record LaboratorySafetyEvaluationRequest(
        SafetyEvaluationStage stage,
        SimulationCommand command,
        SimulationState currentState,
        Optional<SimulationStateDelta> proposedDelta,
        Map<String, String> environmentContext
) {
    public LaboratorySafetyEvaluationRequest {
        Objects.requireNonNull(stage, "stage must not be null");
        Objects.requireNonNull(command, "command must not be null");
        Objects.requireNonNull(currentState, "currentState must not be null");
        Objects.requireNonNull(proposedDelta, "proposedDelta must not be null");
        environmentContext = environmentContext == null ? Map.of() : Map.copyOf(environmentContext);
    }

    public static LaboratorySafetyEvaluationRequest preExecution(SimulationCommand command, SimulationState state, Map<String, String> env) {
        return new LaboratorySafetyEvaluationRequest(SafetyEvaluationStage.PRE_EXECUTION, command, state, Optional.empty(), env);
    }

    public static LaboratorySafetyEvaluationRequest postCalculation(SimulationCommand command, SimulationState state, SimulationStateDelta delta, Map<String, String> env) {
        return new LaboratorySafetyEvaluationRequest(SafetyEvaluationStage.POST_CALCULATION, command, state, Optional.of(delta), env);
    }
}
