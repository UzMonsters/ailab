package com.ailab.chemistry.domain.electrochemistry;

import com.ailab.chemistry.domain.measurement.ScientificMath;

import java.math.BigDecimal;

public record ElectricCharge(BigDecimal inCoulombs) {
    public ElectricCharge {
        if (inCoulombs.compareTo(BigDecimal.ZERO) < 0) {
            throw new ElectrochemicalException(ElectrochemicalErrorCode.INVALID_ELECTROLYSIS_REQUEST, "Charge must be non-negative");
        }
    }

    public static ElectricCharge of(String value, ElectricChargeUnit unit) {
        return new ElectricCharge(new BigDecimal(value).multiply(unit.factorToCoulomb(), ScientificMath.CALCULATION_CONTEXT));
    }
}
