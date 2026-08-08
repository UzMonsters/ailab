package com.ailab.chemistry.domain.gas;

import com.ailab.chemistry.domain.measurement.Pressure;

import java.math.BigDecimal;
import java.util.List;

public record GasMixtureResult(
        GasCalculationStatus status,
        List<MoleFraction> moleFractions,
        List<PartialPressure> partialPressures,
        Pressure partialPressureSum,
        BigDecimal moleFractionSum
) {
}
