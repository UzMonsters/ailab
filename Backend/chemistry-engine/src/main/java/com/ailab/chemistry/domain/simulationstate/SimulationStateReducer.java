package com.ailab.chemistry.domain.simulationstate;

import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEvent;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventType;
import com.ailab.chemistry.domain.laboratoryevent.MaterialDispensedPayload;
import com.ailab.chemistry.domain.laboratoryevent.MaterialMixedPayload;
import com.ailab.chemistry.domain.laboratoryevent.MaterialTransferredPayload;
import com.ailab.chemistry.domain.laboratoryevent.SampleTakenPayload;
import com.ailab.chemistry.domain.laboratoryevent.ScientificOperationAppliedPayload;
import com.ailab.chemistry.domain.laboratoryevent.SessionCreatedPayload;
import com.ailab.chemistry.domain.laboratoryevent.SessionLifecyclePayload;
import com.ailab.chemistry.domain.laboratoryevent.StepCompletedPayload;
import com.ailab.chemistry.domain.laboratoryevent.StepDefinitionSnapshot;
import com.ailab.chemistry.domain.laboratoryevent.StepFailedPayload;
import com.ailab.chemistry.domain.laboratoryevent.StepSkippedPayload;
import com.ailab.chemistry.domain.laboratoryevent.StepStartedPayload;

import com.ailab.chemistry.domain.simulationengine.MaterialStateDelta;
import com.ailab.chemistry.domain.simulationengine.VesselStateDelta;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimulationStateReducer {
    public SimulationState replay(SimulationSessionId sessionId, List<LaboratoryEvent> events) {
        SimulationState state = SimulationState.initial(sessionId);
        for (LaboratoryEvent event : events) {
            state = apply(state, event);
        }
        return state;
    }

    public SimulationState apply(SimulationState state, LaboratoryEvent event) {
        if (!state.sessionId().equals(event.sessionId())) {
            throw failure(SimulationStateErrorCode.STALE_STATE_VERSION, "Event session does not match state session");
        }
        long expectedSequence = state.version().value() + 1;
        if (event.sequence().value() != expectedSequence || event.stateVersion().value() != expectedSequence) {
            throw failure(SimulationStateErrorCode.EVENT_SEQUENCE_GAP,
                    "Events must apply in exact contiguous sequence");
        }
        if (state.status().terminal()) {
            throw failure(SimulationStateErrorCode.TERMINAL_SESSION_IMMUTABLE,
                    "A terminal session is immutable");
        }

        SimulationStateVersion nextVersion = event.stateVersion();
        SimulationClock nextClock = new SimulationClock(event.occurredAt());
        return switch (event.type()) {
            case SESSION_CREATED -> applySessionCreated(state, event, nextVersion, nextClock);
            case SESSION_STARTED -> applyStarted(state, nextVersion, nextClock);
            case SESSION_PAUSED -> requireRunning(state).withStatus(SimulationSessionStatus.PAUSED, nextVersion, nextClock);
            case SESSION_RESUMED -> requireStatus(state, SimulationSessionStatus.PAUSED)
                    .withStatus(SimulationSessionStatus.RUNNING, nextVersion, nextClock);
            case SESSION_COMPLETED -> state.withStatus(SimulationSessionStatus.COMPLETED, nextVersion, nextClock);
            case SESSION_CANCELLED -> state.withStatus(SimulationSessionStatus.CANCELLED, nextVersion, nextClock);
            case SESSION_FAILED -> state.withStatus(SimulationSessionStatus.FAILED, nextVersion, nextClock);
            case STEP_STARTED -> applyStepStarted(requireRunning(state), (StepStartedPayload) event.payload(), nextVersion, nextClock);
            case STEP_COMPLETED -> applyStepCompleted(requireRunning(state), (StepCompletedPayload) event.payload(), nextVersion, nextClock);
            case STEP_FAILED -> applyStepFailed(requireRunning(state), (StepFailedPayload) event.payload(), nextVersion, nextClock);
            case STEP_SKIPPED -> applyStepSkipped(requireRunning(state), (StepSkippedPayload) event.payload(), nextVersion, nextClock);
            case MATERIAL_DISPENSED -> applyDispensed(requireRunning(state), event, (MaterialDispensedPayload) event.payload(), nextVersion, nextClock);
            case MATERIAL_TRANSFERRED, MATERIAL_ADDED -> applyTransferred(requireRunning(state), event, (MaterialTransferredPayload) event.payload(), nextVersion, nextClock);
            case SAMPLE_TAKEN -> applySampleTaken(requireRunning(state), event, (SampleTakenPayload) event.payload(), nextVersion, nextClock);
            case MATERIAL_MIXED -> applyMixed(requireRunning(state), (MaterialMixedPayload) event.payload(), nextVersion, nextClock);
            case STOICHIOMETRIC_REACTION_APPLIED, EQUILIBRIUM_REACTION_APPLIED, KINETIC_PROGRESS_APPLIED,
                 THERMAL_OPERATION_APPLIED, GAS_STATE_CHANGED, PHASE_TRANSITION_APPLIED,
                 ELECTROLYSIS_APPLIED, BOOKKEEPING_MIX_APPLIED ->
                    applyScientificDelta(requireRunning(state), event,
                            (ScientificOperationAppliedPayload) event.payload(), nextVersion, nextClock);
            case PROCESS_ASSIGNED, EQUIPMENT_ALLOCATED, EQUIPMENT_RELEASED, ENVIRONMENT_UPDATED -> state.advanceOnly(nextVersion, nextClock);
        };
    }

    private SimulationState applySessionCreated(SimulationState state, LaboratoryEvent event,
                                                SimulationStateVersion nextVersion, SimulationClock nextClock) {
        if (state.version().value() != 0) {
            throw failure(SimulationStateErrorCode.SESSION_ALREADY_CREATED, "Session creation occurs once");
        }
        SessionCreatedPayload payload = (SessionCreatedPayload) event.payload();
        Map<String, ProcessStepExecution> steps = payload.steps().stream()
                .collect(Collectors.toMap(
                        StepDefinitionSnapshot::stepId,
                        step -> new ProcessStepExecution(step.stepId(), step.optional(), step.dependencies(),
                                ProcessStepExecutionStatus.PENDING, Map.of()),
                        (left, right) -> left,
                        LinkedHashMap::new));
        ProcessExecutionState process = new ProcessExecutionState(payload.processCode(), payload.processVersion(), steps)
                .refreshAvailability();
        return new SimulationState(state.sessionId(), SimulationSessionStatus.CREATED, nextVersion, nextClock, process,
                Map.of(), Map.of(), state.environment());
    }

    private SimulationState applyStarted(SimulationState state, SimulationStateVersion nextVersion, SimulationClock nextClock) {
        if (state.status() != SimulationSessionStatus.CREATED && state.status() != SimulationSessionStatus.PAUSED) {
            throw failure(SimulationStateErrorCode.SESSION_NOT_RUNNING, "Session can start only from created or paused state");
        }
        return state.withStatus(SimulationSessionStatus.RUNNING, nextVersion, nextClock);
    }

    private SimulationState applyStepStarted(SimulationState state, StepStartedPayload payload,
                                             SimulationStateVersion nextVersion, SimulationClock nextClock) {
        ProcessStepExecution step = state.processExecution().step(payload.stepId());
        if (step.status() != ProcessStepExecutionStatus.AVAILABLE) {
            throw failure(SimulationStateErrorCode.STEP_DEPENDENCY_INCOMPLETE,
                    "Step cannot start until all dependencies are complete");
        }
        Map<String, EquipmentAllocation> allocations = new LinkedHashMap<>(state.equipmentAllocations());
        for (String equipmentProfileId : payload.exclusiveEquipmentProfileIds()) {
            if (allocations.containsKey(equipmentProfileId)) {
                throw failure(SimulationStateErrorCode.EQUIPMENT_ALREADY_ALLOCATED,
                        "Exclusive equipment is already allocated: " + equipmentProfileId);
            }
            allocations.put(equipmentProfileId, new EquipmentAllocation(equipmentProfileId, payload.stepId(), true));
        }
        ProcessExecutionState process = state.processExecution()
                .withStep(step.withStatus(ProcessStepExecutionStatus.RUNNING));
        return new SimulationState(state.sessionId(), state.status(), nextVersion, nextClock, process,
                state.vessels(), allocations, state.environment());
    }

    private SimulationState applyStepCompleted(SimulationState state, StepCompletedPayload payload,
                                               SimulationStateVersion nextVersion, SimulationClock nextClock) {
        ProcessStepExecution step = state.processExecution().step(payload.stepId());
        if (step.status() != ProcessStepExecutionStatus.RUNNING) {
            throw failure(SimulationStateErrorCode.STEP_NOT_RUNNING, "Only running steps may complete");
        }
        ProcessExecutionState process = state.processExecution()
                .withStep(step.withOutcome(ProcessStepExecutionStatus.COMPLETED, payload.explicitOutcome()))
                .refreshAvailability();
        Map<String, EquipmentAllocation> allocations = new LinkedHashMap<>(state.equipmentAllocations());
        if (payload.releaseResources()) {
            allocations.entrySet().removeIf(entry -> entry.getValue().stepId().equals(payload.stepId()));
        }
        return new SimulationState(state.sessionId(), state.status(), nextVersion, nextClock, process,
                state.vessels(), allocations, state.environment());
    }

    private SimulationState applyStepFailed(SimulationState state, StepFailedPayload payload,
                                            SimulationStateVersion nextVersion, SimulationClock nextClock) {
        ProcessStepExecution step = state.processExecution().step(payload.stepId());
        if (step.status() != ProcessStepExecutionStatus.RUNNING) {
            throw failure(SimulationStateErrorCode.STEP_NOT_RUNNING, "Only running steps may fail");
        }
        ProcessExecutionState process = state.processExecution()
                .withStep(step.withStatus(ProcessStepExecutionStatus.FAILED));
        return state.withProcess(process, nextVersion, nextClock);
    }

    private SimulationState applyStepSkipped(SimulationState state, StepSkippedPayload payload,
                                             SimulationStateVersion nextVersion, SimulationClock nextClock) {
        ProcessStepExecution step = state.processExecution().step(payload.stepId());
        if (!step.optional()) {
            throw failure(SimulationStateErrorCode.MANDATORY_STEP_CANNOT_SKIP, "Only optional steps may be skipped; mandatory step rejected");
        }
        if (step.status() != ProcessStepExecutionStatus.AVAILABLE && step.status() != ProcessStepExecutionStatus.PENDING) {
            throw failure(SimulationStateErrorCode.STEP_DEPENDENCY_INCOMPLETE, "Only pending or available optional steps may be skipped");
        }
        ProcessExecutionState process = state.processExecution()
                .withStep(step.withStatus(ProcessStepExecutionStatus.SKIPPED))
                .refreshAvailability();
        return state.withProcess(process, nextVersion, nextClock);
    }

    private SimulationState applyDispensed(SimulationState state, LaboratoryEvent event, MaterialDispensedPayload payload,
                                           SimulationStateVersion nextVersion, SimulationClock nextClock) {
        VesselState vessel = state.vessels().getOrDefault(payload.vesselId(),
                VesselState.empty(payload.vesselId(), payload.containerProfileId(), payload.vesselWorkingVolume()));
        vessel = vessel.add(new MaterialPortion(payload.compoundCode(), payload.quantity(), payload.unit(),
                payload.physicalState(), event.eventId().value()));
        return state.withVessel(vessel, nextVersion, nextClock);
    }

    private SimulationState applyTransferred(SimulationState state, LaboratoryEvent event, MaterialTransferredPayload payload,
                                             SimulationStateVersion nextVersion, SimulationClock nextClock) {
        VesselState source = state.vessel(payload.sourceVesselId())
                .subtract(payload.compoundCode(), payload.quantity(), payload.unit(), payload.physicalState());
        VesselState target = state.vessels().getOrDefault(payload.targetVesselId(),
                VesselState.empty(payload.targetVesselId(), "", payload.targetWorkingVolume()))
                .add(new MaterialPortion(payload.compoundCode(), payload.quantity(), payload.unit(), payload.physicalState(),
                        event.eventId().value()));
        Map<String, VesselState> vessels = new LinkedHashMap<>(state.vessels());
        vessels.put(source.vesselId(), source);
        vessels.put(target.vesselId(), target);
        return state.withVessels(vessels, nextVersion, nextClock);
    }

    private SimulationState applySampleTaken(SimulationState state, LaboratoryEvent event, SampleTakenPayload payload,
                                             SimulationStateVersion nextVersion, SimulationClock nextClock) {
        VesselState source = state.vessel(payload.sourceVesselId())
                .subtract(payload.compoundCode(), payload.quantity(), payload.unit(), payload.physicalState());
        VesselState sample = VesselState.empty(payload.sampleId(), "SAMPLE", BigDecimal.ZERO)
                .add(new MaterialPortion(payload.compoundCode(), payload.quantity(), payload.unit(), payload.physicalState(),
                        event.eventId().value()));
        Map<String, VesselState> vessels = new LinkedHashMap<>(state.vessels());
        vessels.put(source.vesselId(), source);
        vessels.put(sample.vesselId(), sample);
        return state.withVessels(vessels, nextVersion, nextClock);
    }

    private SimulationState applyMixed(SimulationState state, MaterialMixedPayload payload,
                                       SimulationStateVersion nextVersion, SimulationClock nextClock) {
        VesselState vessel = state.vessel(payload.vesselId()).withMixingNote(payload.bookkeepingNote());
        return state.withVessel(vessel, nextVersion, nextClock);
    }

    private SimulationState applyScientificDelta(SimulationState state, LaboratoryEvent event,
                                                 ScientificOperationAppliedPayload payload,
                                                 SimulationStateVersion nextVersion, SimulationClock nextClock) {
        Map<String, VesselState> vessels = new LinkedHashMap<>(state.vessels());
        for (VesselStateDelta vesselDelta : payload.stateDelta().vesselDeltas()) {
            VesselState vessel = vessels.getOrDefault(vesselDelta.vesselId(),
                    VesselState.empty(vesselDelta.vesselId(), "", BigDecimal.ZERO));
            for (MaterialStateDelta materialDelta : vesselDelta.materialDeltas()) {
                if (!vessel.vesselId().equals(materialDelta.vesselId())) {
                    throw failure(SimulationStateErrorCode.UNKNOWN_EVENT_TYPE,
                            "Scientific delta references a different vessel than the containing vessel delta");
                }
                if (materialDelta.quantityDelta().compareTo(BigDecimal.ZERO) < 0) {
                    vessel = vessel.subtract(materialDelta.compoundCode(), materialDelta.quantityDelta().abs(),
                            materialDelta.unit(), materialDelta.physicalState());
                } else if (materialDelta.quantityDelta().compareTo(BigDecimal.ZERO) > 0) {
                    vessel = vessel.add(new MaterialPortion(materialDelta.compoundCode(), materialDelta.quantityDelta(),
                            materialDelta.unit(), materialDelta.physicalState(), event.eventId().value()));
                }
            }
            if (!vesselDelta.mixingNote().isBlank()) {
                vessel = vessel.withMixingNote(vesselDelta.mixingNote());
            }
            vessels.put(vessel.vesselId(), vessel);
        }
        return state.withVessels(vessels, nextVersion, nextClock);
    }

    private SimulationState requireRunning(SimulationState state) {
        if (state.status() != SimulationSessionStatus.RUNNING) {
            throw failure(SimulationStateErrorCode.SESSION_NOT_RUNNING,
                    "A paused or non-running session cannot start steps or mutate materials");
        }
        return state;
    }

    private SimulationState requireStatus(SimulationState state, SimulationSessionStatus status) {
        if (state.status() != status) {
            throw failure(SimulationStateErrorCode.SESSION_NOT_RUNNING, "Session status must be " + status);
        }
        return state;
    }

    private SimulationStateException failure(SimulationStateErrorCode code, String message) {
        return new SimulationStateException(code, message);
    }
}
