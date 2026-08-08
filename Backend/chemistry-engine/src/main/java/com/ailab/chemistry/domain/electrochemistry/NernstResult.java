package com.ailab.chemistry.domain.electrochemistry;

import java.math.BigDecimal;
import java.util.List;

public record NernstResult(
        ElectrochemicalStatus status,
        ElectrochemicalCellResult standardCell,
        BigDecimal reactionQuotient,
        BigDecimal lnReactionQuotient,
        CellPotential cellPotential,
        List<String> activitySources,
        List<String> assumptions
) {
    public NernstResult {
        activitySources = List.copyOf(activitySources);
        assumptions = List.copyOf(assumptions);
    }
}
