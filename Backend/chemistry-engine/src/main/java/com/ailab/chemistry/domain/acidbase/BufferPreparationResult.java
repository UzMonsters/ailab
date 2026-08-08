package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.MolarConcentration;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record BufferPreparationResult(
        BufferSystem system,
        BigDecimal targetPh,
        BigDecimal requiredRatio,
        MolarConcentration acidComponentConcentration,
        MolarConcentration baseComponentConcentration,
        AmountOfSubstance acidComponentMoles,
        AmountOfSubstance baseComponentMoles,
        BufferRegionStatus status,
        BufferCalculationMethod calculationMethod,
        List<BufferAssumption> assumptions,
        Map<String, BigDecimal> constants
) {
    public BufferPreparationResult {
        Objects.requireNonNull(system);
        Objects.requireNonNull(targetPh);
        Objects.requireNonNull(requiredRatio);
        Objects.requireNonNull(acidComponentConcentration);
        Objects.requireNonNull(baseComponentConcentration);
        Objects.requireNonNull(acidComponentMoles);
        Objects.requireNonNull(baseComponentMoles);
        Objects.requireNonNull(status);
        Objects.requireNonNull(calculationMethod);
        assumptions = List.copyOf(assumptions == null ? List.of() : assumptions);
        constants = Map.copyOf(constants == null ? Map.of() : constants);
    }

    public BufferRegionStatus getStatus() { return status; }
    public BigDecimal getRequiredRatio() { return requiredRatio; }
    public MolarConcentration getAcidComponentConcentration() { return acidComponentConcentration; }
    public MolarConcentration getBaseComponentConcentration() { return baseComponentConcentration; }
    public AmountOfSubstance getAcidComponentMoles() { return acidComponentMoles; }
    public AmountOfSubstance getBaseComponentMoles() { return baseComponentMoles; }
}
