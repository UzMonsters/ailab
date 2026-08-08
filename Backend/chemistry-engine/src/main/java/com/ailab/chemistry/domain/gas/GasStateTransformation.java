package com.ailab.chemistry.domain.gas;

import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.Volume;

public record GasStateTransformation(
        GasTransformationConstraint constraint,
        Pressure initialPressure,
        Volume initialVolume,
        Temperature initialTemperature,
        Pressure finalPressure,
        Volume finalVolume,
        Temperature finalTemperature
) {
    public GasStateTransformation {
        if (constraint == null) {
            throw new GasLawException(GasLawErrorCode.MISSING_PROCESS_CONSTRAINT, "Gas transformation must declare a process constraint");
        }
    }

    public static GasStateTransformation solveFinalVolume(
            GasTransformationConstraint constraint,
            Pressure p1,
            Volume v1,
            Temperature t1,
            Pressure p2,
            Volume v2,
            Temperature t2) {
        return new GasStateTransformation(constraint, p1, v1, t1, p2, v2, t2);
    }

    public static GasStateTransformation solveFinalPressure(
            GasTransformationConstraint constraint,
            Pressure p1,
            Volume v1,
            Temperature t1,
            Pressure p2,
            Volume v2,
            Temperature t2) {
        return new GasStateTransformation(constraint, p1, v1, t1, p2, v2, t2);
    }
}
