package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;

public enum ElectricalConductivityUnit {
    SIEMENS_PER_METER("S/m", BigDecimal.ONE),
    MILLISIEMENS_PER_CENTIMETER("mS/cm", new BigDecimal("0.1"));

    private final String symbol;
    private final BigDecimal factorToCanonical;

    ElectricalConductivityUnit(String symbol, BigDecimal factorToCanonical) {
        this.symbol = symbol;
        this.factorToCanonical = factorToCanonical;
    }

    public String getSymbol() { return symbol; }
    public BigDecimal getFactorToCanonical() { return factorToCanonical; }
}
