package com.ailab.chemistry.domain.electrochemistry;

import java.math.BigDecimal;

public record CurrentEfficiency(BigDecimal fraction) {
    public CurrentEfficiency {
        if (fraction.compareTo(BigDecimal.ZERO) <= 0 || fraction.compareTo(BigDecimal.ONE) > 0) {
            throw new ElectrochemicalException(ElectrochemicalErrorCode.INVALID_CURRENT_EFFICIENCY, "Current efficiency must satisfy 0 < efficiency <= 1");
        }
    }

    public static CurrentEfficiency of(String value) {
        return new CurrentEfficiency(new BigDecimal(value));
    }
}
