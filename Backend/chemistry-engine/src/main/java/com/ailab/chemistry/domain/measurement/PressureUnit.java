package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;
import com.ailab.chemistry.domain.measurement.exception.IncompatibleUnitException;

public enum PressureUnit {
    PASCAL("Pa", BigDecimal.ONE),
    KILOPASCAL("kPa", new BigDecimal("1000")),
    ATMOSPHERE("atm", new BigDecimal("101325")),
    BAR("bar", new BigDecimal("100000"));

    private final String symbol;
    private final BigDecimal factor;

    PressureUnit(String symbol, BigDecimal factor) {
        this.symbol = symbol;
        this.factor = factor;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getFactor() {
        return factor;
    }

    public static PressureUnit fromSymbol(String symbol) {
        if (symbol == null) {
            throw new IllegalArgumentException("Symbol must not be null");
        }
        for (PressureUnit unit : values()) {
            if (unit.symbol.equalsIgnoreCase(symbol)) {
                return unit;
            }
        }
        throw new IncompatibleUnitException("Unknown pressure unit symbol: " + symbol);
    }
}
