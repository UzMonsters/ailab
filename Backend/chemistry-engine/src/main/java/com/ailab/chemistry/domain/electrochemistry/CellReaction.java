package com.ailab.chemistry.domain.electrochemistry;

import java.math.BigDecimal;
import java.util.Map;

public record CellReaction(
        Map<String, BigDecimal> terms,
        BigDecimal atomResidual,
        BigDecimal chargeResidual
) {
    public CellReaction {
        terms = Map.copyOf(terms);
    }
}
