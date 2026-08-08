package com.ailab.chemistry.domain.measurement;

import com.ailab.chemistry.domain.measurement.exception.IncompatibleUnitException;

public enum ConcentrationBasis {
    MASS_PER_MASS("w/w"),
    MASS_PER_VOLUME("w/v"),
    VOLUME_PER_VOLUME("v/v");

    private final String symbol;

    ConcentrationBasis(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public static ConcentrationBasis fromSymbol(String symbol) {
        if (symbol == null) {
            throw new IllegalArgumentException("Symbol must not be null");
        }
        for (ConcentrationBasis basis : values()) {
            if (basis.symbol.equalsIgnoreCase(symbol)) {
                return basis;
            }
        }
        throw new IncompatibleUnitException("Unknown concentration basis symbol: " + symbol);
    }
}
