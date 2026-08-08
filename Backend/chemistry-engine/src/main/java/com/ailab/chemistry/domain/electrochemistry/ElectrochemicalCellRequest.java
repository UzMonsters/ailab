package com.ailab.chemistry.domain.electrochemistry;

import java.math.BigDecimal;

public record ElectrochemicalCellRequest(
        String cathodeReductionRecordId,
        String anodeReductionRecordId,
        ElectrochemicalCellType cellType,
        BigDecimal reactionScale
) {
    public ElectrochemicalCellRequest {
        if (reactionScale == null || reactionScale.compareTo(BigDecimal.ZERO) <= 0) {
            reactionScale = BigDecimal.ONE;
        }
    }
}
