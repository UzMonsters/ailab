package com.ailab.chemistry.domain.kinetics;

import com.ailab.chemistry.domain.measurement.MolarEnergy;
import com.ailab.chemistry.domain.measurement.MolarEnergyUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;

import java.math.BigDecimal;
import java.util.Objects;

public record ArrheniusParameters(
        BigDecimal preExponentialFactorA,
        BigDecimal temperatureExponentN,
        Temperature referenceTemperature,
        MolarEnergy activationEnergy,
        Temperature minTemperature,
        Temperature maxTemperature,
        String expressionForm) {

    public ArrheniusParameters {
        Objects.requireNonNull(preExponentialFactorA, "preExponentialFactorA must not be null");
        Objects.requireNonNull(activationEnergy, "activationEnergy must not be null");
        temperatureExponentN = temperatureExponentN == null ? BigDecimal.ZERO : temperatureExponentN;
        referenceTemperature = referenceTemperature == null ? Temperature.of("298.15", TemperatureUnit.KELVIN) : referenceTemperature;
        expressionForm = expressionForm == null ? (temperatureExponentN.compareTo(BigDecimal.ZERO) == 0 ? "STANDARD_ARRHENIUS" : "MODIFIED_ARRHENIUS") : expressionForm;

        if (preExponentialFactorA.compareTo(BigDecimal.ZERO) <= 0) {
            throw new KineticException(
                    KineticErrorCode.INVALID_RATE_CONSTANT,
                    "Pre-exponential factor A must be positive: " + preExponentialFactorA);
        }
        if (activationEnergy.in(MolarEnergyUnit.JOULE_PER_MOLE).compareTo(BigDecimal.ZERO) < 0) {
            throw new KineticException(
                    KineticErrorCode.INVALID_ACTIVATION_ENERGY,
                    "Activation energy cannot be negative: " + activationEnergy);
        }
    }

    public ArrheniusParameters(
            BigDecimal preExponentialFactorA,
            MolarEnergy activationEnergy,
            Temperature minTemperature,
            Temperature maxTemperature) {
        this(preExponentialFactorA, BigDecimal.ZERO, Temperature.of("298.15", TemperatureUnit.KELVIN), activationEnergy, minTemperature, maxTemperature, "STANDARD_ARRHENIUS");
    }
}
