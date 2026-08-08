package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;

public enum MolarEnergyUnit {
    JOULE_PER_MOLE("J/mol", BigDecimal.ONE),
    KILOJOULE_PER_MOLE("kJ/mol", new BigDecimal("1000"));

    private final String symbol;
    private final BigDecimal factorToCanonical;

    MolarEnergyUnit(String symbol, BigDecimal factorToCanonical) {
        this.symbol = symbol;
        this.factorToCanonical = factorToCanonical;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getFactorToCanonical() {
        return factorToCanonical;
    }
}
