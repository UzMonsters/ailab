package com.ailab.chemistry.domain.simulationengine;

import java.math.BigDecimal;
import java.util.List;

public record VesselStateDelta(
        String vesselId,
        List<MaterialStateDelta> materialDeltas,
        String mixingNote,
        BigDecimal finalTemperatureKelvin,
        BigDecimal finalPressureKpa,
        BigDecimal finalVolumeMl
) {
    public VesselStateDelta {
        if (vesselId == null || vesselId.isBlank()) {
            throw new SimulationExecutionException(SimulationExecutionErrorCode.STATE_DELTA_INVARIANT_FAILED,
                    "Vessel delta requires a vessel id");
        }
        materialDeltas = List.copyOf(materialDeltas == null ? List.of() : materialDeltas);
        mixingNote = mixingNote == null ? "" : mixingNote;
    }
}
