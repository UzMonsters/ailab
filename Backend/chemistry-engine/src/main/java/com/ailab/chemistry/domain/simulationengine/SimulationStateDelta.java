package com.ailab.chemistry.domain.simulationengine;

import java.util.List;

public record SimulationStateDelta(
        List<VesselStateDelta> vesselDeltas,
        ConservationLedger conservationLedger
) {
    public SimulationStateDelta {
        vesselDeltas = List.copyOf(vesselDeltas == null ? List.of() : vesselDeltas);
        conservationLedger = conservationLedger == null ? ConservationLedger.notApplicable() : conservationLedger;
    }
}
