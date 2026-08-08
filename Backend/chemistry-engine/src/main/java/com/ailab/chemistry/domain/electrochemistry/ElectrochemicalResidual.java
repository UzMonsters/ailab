package com.ailab.chemistry.domain.electrochemistry;

import java.math.BigDecimal;

public record ElectrochemicalResidual(BigDecimal atomResidual, BigDecimal chargeResidual, BigDecimal thermodynamicResidual) {
}
