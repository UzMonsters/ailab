package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;
import com.ailab.chemistry.domain.measurement.exception.IncompatibleUnitException;

public enum MassConcentrationUnit {
    GRAM_PER_LITER("g/L", BigDecimal.ONE),
    MILLIGRAM_PER_LITER("mg/L", new BigDecimal("0.001"));

    private final String symbol;
    private final BigDecimal factor;

    MassConcentrationUnit(String symbol, BigDecimal factor) {
        this.symbol = symbol;
        this.factor = factor;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getFactor() {
        return factor;
    }

    public static MassConcentrationUnit fromSymbol(String symbol) {
        if (symbol == null) {
            throw new IllegalArgumentException("Symbol must not be null");
        }
        for (MassConcentrationUnit unit : values()) {
            if (unit.symbol.equalsIgnoreCase(symbol)) {
                return unit;
            }
        }
        throw new IncompatibleUnitException("Unknown mass concentration unit symbol: " + symbol);
    }
}
