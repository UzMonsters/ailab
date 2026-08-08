package com.ailab.chemistry.domain.gas;

import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.Volume;

public record GasStateRequest(
        GasEquationModel model,
        Pressure pressure,
        Volume volume,
        AmountOfSubstance amount,
        Temperature temperature,
        CompressibilityFactor compressibilityFactor
) {
    public static GasStateRequest solvePressure(GasEquationModel model, Volume volume, AmountOfSubstance amount, Temperature temperature, CompressibilityFactor z) {
        return new GasStateRequest(model, null, volume, amount, temperature, z);
    }

    public static GasStateRequest solveVolume(GasEquationModel model, Pressure pressure, AmountOfSubstance amount, Temperature temperature, CompressibilityFactor z) {
        return new GasStateRequest(model, pressure, null, amount, temperature, z);
    }

    public static GasStateRequest solveAmount(GasEquationModel model, Pressure pressure, Volume volume, Temperature temperature, CompressibilityFactor z) {
        return new GasStateRequest(model, pressure, volume, null, temperature, z);
    }

    public static GasStateRequest solveTemperature(GasEquationModel model, Pressure pressure, Volume volume, AmountOfSubstance amount, CompressibilityFactor z) {
        return new GasStateRequest(model, pressure, volume, amount, null, z);
    }

    public static GasStateRequest validate(GasEquationModel model, Pressure pressure, Volume volume, AmountOfSubstance amount, Temperature temperature, CompressibilityFactor z) {
        return new GasStateRequest(model, pressure, volume, amount, temperature, z);
    }
}
