package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;

public enum SpecificHeatCapacityUnit {
    JOULE_PER_KILOGRAM_KELVIN("J/(kg·K)", BigDecimal.ONE),
    JOULE_PER_GRAM_KELVIN("J/(g·K)", new BigDecimal("1000")),
    KILOJOULE_PER_KILOGRAM_KELVIN("kJ/(kg·K)", new BigDecimal("1000"));

    private final String symbol;
    private final BigDecimal factorToCanonical;

    SpecificHeatCapacityUnit(String symbol, BigDecimal factorToCanonical) {
        this.symbol = symbol;
        this.factorToCanonical = factorToCanonical;
    }

    public String getSymbol() { return symbol; }
    public BigDecimal getFactorToCanonical() { return factorToCanonical; }
}
