package com.ailab.chemistry.domain.solubility;

import java.math.BigDecimal;
import java.util.Objects;

public record SaturationRatio(BigDecimal value) {
    public SaturationRatio {
        Objects.requireNonNull(value, "saturation ratio must not be null");
    }
}
