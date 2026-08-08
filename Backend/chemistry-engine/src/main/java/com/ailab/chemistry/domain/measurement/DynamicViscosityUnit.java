package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;

public enum DynamicViscosityUnit {
    PASCAL_SECOND("Pa·s", BigDecimal.ONE),
    MILLIPASCAL_SECOND("mPa·s", new BigDecimal("0.001")),
    CENTIPOISE("cP", new BigDecimal("0.001"));

    private final String symbol;
    private final BigDecimal factorToCanonical;

    DynamicViscosityUnit(String symbol, BigDecimal factorToCanonical) {
        this.symbol = symbol;
        this.factorToCanonical = factorToCanonical;
    }

    public String getSymbol() { return symbol; }
    public BigDecimal getFactorToCanonical() { return factorToCanonical; }
}
