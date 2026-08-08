package com.ailab.chemistry.domain.electrochemistry;

import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.Mass;

public record ElectrolysisResult(
        ElectrochemicalStatus status,
        ElectricCharge charge,
        ElectricCharge effectiveCharge,
        AmountOfSubstance electronAmount,
        AmountOfSubstance substanceAmount,
        Mass mass,
        String signConvention,
        ElectrochemicalEquivalent equivalent,
        FaradayConstant faradayConstant
) {
}
