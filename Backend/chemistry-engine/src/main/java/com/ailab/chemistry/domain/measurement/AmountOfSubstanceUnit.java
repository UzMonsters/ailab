package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;
import com.ailab.chemistry.domain.measurement.exception.IncompatibleUnitException;

public enum AmountOfSubstanceUnit {
    MILLIMOLE("mmol", new BigDecimal("0.001")),
    MOLE("mol", BigDecimal.ONE);

    private final String symbol;
    private final BigDecimal factor;

    AmountOfSubstanceUnit(String symbol, BigDecimal factor) {
        this.symbol = symbol;
        this.factor = factor;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getFactor() {
        return factor;
    }

    public static AmountOfSubstanceUnit fromSymbol(String symbol) {
        if (symbol == null) {
            throw new IllegalArgumentException("Symbol must not be null");
        }
        for (AmountOfSubstanceUnit unit : values()) {
            if (unit.symbol.equalsIgnoreCase(symbol)) {
                return unit;
            }
        }
        throw new IncompatibleUnitException("Unknown amount of substance unit symbol: " + symbol);
    }
}
