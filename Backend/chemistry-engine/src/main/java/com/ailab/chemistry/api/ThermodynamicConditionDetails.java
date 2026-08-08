package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.thermodynamics.StandardStateConvention;

import java.math.BigDecimal;

public record ThermodynamicConditionDetails(
        BigDecimal temperatureKelvin,
        BigDecimal pressurePascal,
        MatterState state,
        StandardStateConvention standardStateConvention) {
}
