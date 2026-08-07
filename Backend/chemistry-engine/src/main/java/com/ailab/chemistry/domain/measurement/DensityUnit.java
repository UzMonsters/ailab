package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;

public enum DensityUnit {
    KILOGRAM_PER_CUBIC_METER("kg/m³", BigDecimal.ONE),
    GRAM_PER_CUBIC_CENTIMETER("g/cm³", new BigDecimal("1000")),
    GRAM_PER_LITER("g/L", BigDecimal.ONE);

    private final String symbol;
    private final BigDecimal factorToKgPerM3;

    DensityUnit(String symbol, BigDecimal factorToKgPerM3) {
        this.symbol = symbol;
        this.factorToKgPerM3 = factorToKgPerM3;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getFactorToKgPerM3() {
        return factorToKgPerM3;
    }
}
