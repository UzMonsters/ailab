package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;

public enum MolarEntropyUnit {
    JOULE_PER_MOLE_KELVIN("J/(mol*K)", BigDecimal.ONE),
    KILOJOULE_PER_MOLE_KELVIN("kJ/(mol*K)", new BigDecimal("1000"));

    private final String symbol;
    private final BigDecimal factorToCanonical;

    MolarEntropyUnit(String symbol, BigDecimal factorToCanonical) {
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
