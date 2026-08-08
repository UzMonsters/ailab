package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;

public enum SurfaceTensionUnit {
    NEWTON_PER_METER("N/m", BigDecimal.ONE),
    MILLI_NEWTON_PER_METER("mN/m", new BigDecimal("0.001"));

    private final String symbol;
    private final BigDecimal factorToCanonical;

    SurfaceTensionUnit(String symbol, BigDecimal factorToCanonical) {
        this.symbol = symbol;
        this.factorToCanonical = factorToCanonical;
    }

    public String getSymbol() { return symbol; }
    public BigDecimal getFactorToCanonical() { return factorToCanonical; }
}
