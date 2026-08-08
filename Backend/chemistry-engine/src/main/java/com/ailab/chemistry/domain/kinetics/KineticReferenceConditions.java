package com.ailab.chemistry.domain.kinetics;

import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.Temperature;

import java.math.BigDecimal;

public record KineticReferenceConditions(
        Temperature temperature,
        Pressure pressure,
        String solvent,
        String catalyst,
        BigDecimal pH,
        BigDecimal ionicStrength) {
    public KineticReferenceConditions {
        solvent = solvent == null ? "WATER" : solvent;
    }
}
