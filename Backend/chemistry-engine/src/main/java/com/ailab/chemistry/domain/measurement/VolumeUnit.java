package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;
import com.ailab.chemistry.domain.measurement.exception.IncompatibleUnitException;

public enum VolumeUnit {
    MICROLITER("µL", new BigDecimal("0.000001")),
    MILLILITER("mL", new BigDecimal("0.001")),
    LITER("L", BigDecimal.ONE);

    private final String symbol;
    private final BigDecimal factor;

    VolumeUnit(String symbol, BigDecimal factor) {
        this.symbol = symbol;
        this.factor = factor;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getFactor() {
        return factor;
    }

    public static VolumeUnit fromSymbol(String symbol) {
        if (symbol == null) {
            throw new IllegalArgumentException("Symbol must not be null");
        }
        String normSymbol = symbol.trim();
        if (normSymbol.equalsIgnoreCase("µL") || normSymbol.equalsIgnoreCase("μL") || normSymbol.equalsIgnoreCase("uL")) {
            return MICROLITER;
        }
        for (VolumeUnit unit : values()) {
            if (unit.symbol.equalsIgnoreCase(normSymbol)) {
                return unit;
            }
        }
        throw new IncompatibleUnitException("Unknown volume unit symbol: " + symbol);
    }
}
