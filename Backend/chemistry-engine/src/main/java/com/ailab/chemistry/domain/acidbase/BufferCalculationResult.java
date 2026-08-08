package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.PhValue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class BufferCalculationResult {
    private final BufferSystem system;
    private final BufferComponent acidComponent;
    private final BufferComponent baseComponent;
    private final PhValue ph;
    private final PhValue poh;
    private final BigDecimal componentRatio;
    private final MolarConcentration totalBufferConcentration;
    private final BufferCapacity capacity;
    private final BufferRegionStatus status;
    private final BufferCalculationMethod calculationMethod;
    private final List<BufferAssumption> assumptions;
    private final Map<String, BigDecimal> constants;
    private final List<String> sources;
    private final String applicability;

    public BufferCalculationResult(
            BufferSystem system,
            BufferComponent acidComponent,
            BufferComponent baseComponent,
            PhValue ph,
            PhValue poh,
            BigDecimal componentRatio,
            MolarConcentration totalBufferConcentration,
            BufferCapacity capacity,
            BufferRegionStatus status,
            BufferCalculationMethod calculationMethod,
            List<BufferAssumption> assumptions,
            Map<String, BigDecimal> constants,
            List<String> sources,
            String applicability) {
        this.system = Objects.requireNonNull(system);
        this.acidComponent = Objects.requireNonNull(acidComponent);
        this.baseComponent = Objects.requireNonNull(baseComponent);
        this.ph = Objects.requireNonNull(ph);
        this.poh = Objects.requireNonNull(poh);
        this.componentRatio = Objects.requireNonNull(componentRatio);
        this.totalBufferConcentration = Objects.requireNonNull(totalBufferConcentration);
        this.capacity = Objects.requireNonNull(capacity);
        this.status = Objects.requireNonNull(status);
        this.calculationMethod = Objects.requireNonNull(calculationMethod);
        this.assumptions = List.copyOf(Objects.requireNonNull(assumptions));
        this.constants = Map.copyOf(Objects.requireNonNull(constants));
        this.sources = List.copyOf(Objects.requireNonNull(sources));
        this.applicability = applicability == null ? "" : applicability;
    }

    public BufferSystem getSystem() { return system; }
    public BufferComponent getAcidComponent() { return acidComponent; }
    public BufferComponent getBaseComponent() { return baseComponent; }
    public PhValue getPh() { return ph; }
    public PhValue getPoh() { return poh; }
    public BigDecimal getComponentRatio() { return componentRatio; }
    public MolarConcentration getTotalBufferConcentration() { return totalBufferConcentration; }
    public BufferCapacity getCapacity() { return capacity; }
    public BufferRegionStatus getStatus() { return status; }
    public BufferCalculationMethod getCalculationMethod() { return calculationMethod; }
    public List<BufferAssumption> getAssumptions() { return assumptions; }
    public Map<String, BigDecimal> getConstants() { return constants; }
    public List<String> getSources() { return sources; }
    public String getApplicability() { return applicability; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BufferCalculationResult that)) return false;
        return system.equals(that.system)
                && acidComponent.equals(that.acidComponent)
                && baseComponent.equals(that.baseComponent)
                && ph.equals(that.ph)
                && poh.equals(that.poh)
                && componentRatio.compareTo(that.componentRatio) == 0
                && totalBufferConcentration.equals(that.totalBufferConcentration)
                && capacity.equals(that.capacity)
                && status == that.status
                && calculationMethod == that.calculationMethod
                && assumptions.equals(that.assumptions)
                && constants.equals(that.constants)
                && sources.equals(that.sources)
                && applicability.equals(that.applicability);
    }

    @Override
    public int hashCode() {
        return Objects.hash(system, acidComponent, baseComponent, ph, poh, componentRatio.stripTrailingZeros(), totalBufferConcentration, capacity, status, calculationMethod, assumptions, constants, sources, applicability);
    }
}
