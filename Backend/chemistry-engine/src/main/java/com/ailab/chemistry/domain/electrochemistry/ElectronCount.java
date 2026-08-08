package com.ailab.chemistry.domain.electrochemistry;

import java.math.BigDecimal;
import java.util.Objects;

public record ElectronCount(BigDecimal value) {
    public ElectronCount {
        Objects.requireNonNull(value, "value must not be null");
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ElectrochemicalException(ElectrochemicalErrorCode.INVALID_HALF_REACTION, "Electron count must be positive");
        }
    }

    public static ElectronCount of(String value) {
        return new ElectronCount(new BigDecimal(value));
    }
}
