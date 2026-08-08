package com.ailab.chemistry.domain.simulationstate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public record SimulationState(
        SimulationSessionId sessionId,
        SimulationSessionStatus status,
        SimulationStateVersion version,
        SimulationClock clock,
        ProcessExecutionState processExecution,
        Map<String, VesselState> vessels,
        Map<String, EquipmentAllocation> equipmentAllocations,
        EnvironmentState environment
) {
    public SimulationState {
        if (sessionId == null || status == null || version == null || clock == null
                || processExecution == null || environment == null) {
            throw new IllegalArgumentException("Simulation state fields are required");
        }
        vessels = Map.copyOf(vessels == null ? Map.of() : vessels);
        equipmentAllocations = Map.copyOf(equipmentAllocations == null ? Map.of() : equipmentAllocations);
    }

    public static SimulationState initial(SimulationSessionId sessionId) {
        return new SimulationState(sessionId, SimulationSessionStatus.READY, new SimulationStateVersion(0),
                new SimulationClock(Instant.EPOCH), ProcessExecutionState.none(), Map.of(), Map.of(),
                EnvironmentState.unknown());
    }

    public ProcessStepExecution step(String stepId) {
        return processExecution.step(stepId);
    }

    public VesselState vessel(String vesselId) {
        VesselState vessel = vessels.get(vesselId);
        if (vessel == null) {
            throw new SimulationStateException(SimulationStateErrorCode.MATERIAL_TRANSFER_EXCEEDS_AVAILABLE,
                    "Unknown vessel: " + vesselId);
        }
        return vessel;
    }

    public BigDecimal quantity(String vesselId, String compoundCode, String unit) {
        VesselState vessel = vessels.get(vesselId);
        return vessel == null ? BigDecimal.ZERO : vessel.quantity(compoundCode, unit);
    }

    public BigDecimal quantity(String vesselId, String compoundCode, String unit, String physicalState) {
        VesselState vessel = vessels.get(vesselId);
        return vessel == null ? BigDecimal.ZERO : vessel.quantity(compoundCode, unit, physicalState);
    }

    public BigDecimal totalQuantity(String compoundCode, String unit) {
        return vessels.values().stream()
                .map(vessel -> vessel.quantity(compoundCode, unit))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public SimulationState withStatus(SimulationSessionStatus status, SimulationStateVersion version, SimulationClock clock) {
        return new SimulationState(sessionId, status, version, clock, processExecution, vessels, equipmentAllocations, environment);
    }

    public SimulationState withProcess(ProcessExecutionState processExecution, SimulationStateVersion version, SimulationClock clock) {
        return new SimulationState(sessionId, status, version, clock, processExecution, vessels, equipmentAllocations, environment);
    }

    public SimulationState withVessel(VesselState vessel, SimulationStateVersion version, SimulationClock clock) {
        Map<String, VesselState> next = new LinkedHashMap<>(vessels);
        next.put(vessel.vesselId(), vessel);
        return new SimulationState(sessionId, status, version, clock, processExecution, next, equipmentAllocations, environment);
    }

    public SimulationState withVessels(Map<String, VesselState> nextVessels, SimulationStateVersion version, SimulationClock clock) {
        return new SimulationState(sessionId, status, version, clock, processExecution, nextVessels, equipmentAllocations, environment);
    }

    public SimulationState withEquipmentAllocations(Map<String, EquipmentAllocation> allocations, SimulationStateVersion version, SimulationClock clock) {
        return new SimulationState(sessionId, status, version, clock, processExecution, vessels, allocations, environment);
    }

    public SimulationState advanceOnly(SimulationStateVersion version, SimulationClock clock) {
        return new SimulationState(sessionId, status, version, clock, processExecution, vessels, equipmentAllocations, environment);
    }
}
