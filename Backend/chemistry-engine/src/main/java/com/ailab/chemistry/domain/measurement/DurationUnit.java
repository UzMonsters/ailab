package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;
import com.ailab.chemistry.domain.measurement.exception.IncompatibleUnitException;

public enum DurationUnit {
    MILLISECOND("ms", new BigDecimal("0.001")),
    SECOND("s", BigDecimal.ONE),
    MINUTE("min", new BigDecimal("60")),
    HOUR("h", new BigDecimal("3600"));

    private final String symbol;
    private final BigDecimal factor;

    DurationUnit(String symbol, BigDecimal factor) {
        this.symbol = symbol;
        this.factor = factor;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getFactor() {
        return factor;
    }

    public static DurationUnit fromSymbol(String symbol) {
        if (symbol == null) {
            throw new IllegalArgumentException("Symbol must not be null");
        }
        for (DurationUnit unit : values()) {
            if (unit.symbol.equalsIgnoreCase(symbol)) {
                return unit;
            }
        }
        throw new IncompatibleUnitException("Unknown duration unit symbol: " + symbol);
    }
}
