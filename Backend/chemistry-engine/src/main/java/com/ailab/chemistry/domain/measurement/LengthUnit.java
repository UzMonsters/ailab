package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;
import java.util.Objects;

public enum LengthUnit {
    PICOMETER("pm", new BigDecimal("0.000000000001")),
    NANOMETER("nm", new BigDecimal("0.000000001")),
    METER("m", BigDecimal.ONE);

    private final String symbol;
    private final BigDecimal factorToMeter;

    LengthUnit(String symbol, BigDecimal factorToMeter) {
        this.symbol = symbol;
        this.factorToMeter = factorToMeter;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getFactorToMeter() {
        return factorToMeter;
    }
}
