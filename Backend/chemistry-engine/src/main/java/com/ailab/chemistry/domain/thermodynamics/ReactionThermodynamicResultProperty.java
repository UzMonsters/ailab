package com.ailab.chemistry.domain.thermodynamics;

import java.math.BigDecimal;
import java.util.Objects;

public record ReactionThermodynamicResultProperty(
        ReactionThermodynamicProperty property,
        BigDecimal value,
        String unitSymbol,
        ThermodynamicSign sign) {

    public ReactionThermodynamicResultProperty(ReactionThermodynamicProperty property, BigDecimal value, String unitSymbol) {
        this(property, value, unitSymbol, ThermodynamicSign.of(value));
    }

    public ReactionThermodynamicResultProperty {
        Objects.requireNonNull(property, "property must not be null");
        Objects.requireNonNull(value, "value must not be null");
        Objects.requireNonNull(unitSymbol, "unitSymbol must not be null");
        Objects.requireNonNull(sign, "sign must not be null");
    }

    public ReactionThermodynamicResultProperty scale(java.math.BigDecimal scalar) {
        return new ReactionThermodynamicResultProperty(property, value.multiply(scalar, com.ailab.chemistry.domain.measurement.ScientificMath.CALCULATION_CONTEXT), unitSymbol);
    }
}
