package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.Temperature;

import java.math.BigDecimal;
import java.util.Objects;

public record BufferCapacity(
        BigDecimal approximateCapacity,
        MolarConcentration totalBufferConcentration,
        BigDecimal acidBaseRatio,
        Temperature temperature,
        BufferCalculationMethod method,
        String limitation
) {
    public BufferCapacity {
        Objects.requireNonNull(approximateCapacity, "approximateCapacity must not be null");
        Objects.requireNonNull(totalBufferConcentration, "totalBufferConcentration must not be null");
        Objects.requireNonNull(acidBaseRatio, "acidBaseRatio must not be null");
        Objects.requireNonNull(temperature, "temperature must not be null");
        Objects.requireNonNull(method, "method must not be null");
        limitation = limitation == null || limitation.isBlank()
                ? "Ideal monoprotic buffer capacity approximation; no activity-coefficient correction."
                : limitation.trim();
    }

    public BigDecimal getApproximateCapacity() {
        return approximateCapacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BufferCapacity that)) return false;
        return approximateCapacity.compareTo(that.approximateCapacity) == 0
                && totalBufferConcentration.equals(that.totalBufferConcentration)
                && acidBaseRatio.compareTo(that.acidBaseRatio) == 0
                && temperature.equals(that.temperature)
                && method == that.method
                && limitation.equals(that.limitation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(approximateCapacity.stripTrailingZeros(), totalBufferConcentration, acidBaseRatio.stripTrailingZeros(), temperature, method, limitation);
    }
}
