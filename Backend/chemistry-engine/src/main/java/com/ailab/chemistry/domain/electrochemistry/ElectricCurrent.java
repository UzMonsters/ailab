package com.ailab.chemistry.domain.electrochemistry;

import com.ailab.chemistry.domain.measurement.ScientificMath;

import java.math.BigDecimal;

public record ElectricCurrent(BigDecimal inAmperes) {
    public ElectricCurrent {
        if (inAmperes.compareTo(BigDecimal.ZERO) < 0) {
            throw new ElectrochemicalException(ElectrochemicalErrorCode.INVALID_ELECTROLYSIS_REQUEST, "Current must be non-negative");
        }
    }

    public static ElectricCurrent of(String value, ElectricCurrentUnit unit) {
        return new ElectricCurrent(new BigDecimal(value).multiply(unit.factorToAmpere(), ScientificMath.CALCULATION_CONTEXT));
    }
}
