package com.ailab.chemistry.domain.thermodynamics;

import java.math.BigDecimal;
import java.util.Objects;

public record ReactionThermodynamicSourceProperty(
        ThermodynamicPropertyType sourceType,
        BigDecimal value,
        String unitSymbol,
        ThermodynamicProvenance provenance) {

    public ReactionThermodynamicSourceProperty {
        Objects.requireNonNull(sourceType, "sourceType must not be null");
        Objects.requireNonNull(value, "value must not be null");
        Objects.requireNonNull(unitSymbol, "unitSymbol must not be null");
        Objects.requireNonNull(provenance, "provenance must not be null");
    }
}
