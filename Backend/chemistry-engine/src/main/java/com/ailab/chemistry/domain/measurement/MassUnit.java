package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;
import com.ailab.chemistry.domain.measurement.exception.IncompatibleUnitException;

public enum MassUnit {
    MILLIGRAM("mg", new BigDecimal("0.001")),
    GRAM("g", BigDecimal.ONE),
    KILOGRAM("kg", new BigDecimal("1000"));

    private final String symbol;
    private final BigDecimal factor;

    MassUnit(String symbol, BigDecimal factor) {
        this.symbol = symbol;
        this.factor = factor;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getFactor() {
        return factor;
    }

    public static MassUnit fromSymbol(String symbol) {
        if (symbol == null) {
            throw new IllegalArgumentException("Symbol must not be null");
        }
        for (MassUnit unit : values()) {
            if (unit.symbol.equalsIgnoreCase(symbol)) {
                return unit;
            }
        }
        throw new IncompatibleUnitException("Unknown mass unit symbol: " + symbol);
    }
}
