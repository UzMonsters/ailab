package com.ailab.chemistry.domain.measurement;

import com.ailab.chemistry.domain.measurement.exception.IncompatibleUnitException;

public enum TemperatureUnit {
    KELVIN("K"),
    CELSIUS("°C");

    private final String symbol;

    TemperatureUnit(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public static TemperatureUnit fromSymbol(String symbol) {
        if (symbol == null) {
            throw new IllegalArgumentException("Symbol must not be null");
        }
        String normSymbol = symbol.trim();
        if (normSymbol.equalsIgnoreCase("K")) {
            return KELVIN;
        }
        if (normSymbol.equalsIgnoreCase("°C") || normSymbol.equalsIgnoreCase("C") || normSymbol.equals("degC")) {
            return CELSIUS;
        }
        throw new IncompatibleUnitException("Unknown temperature unit symbol: " + symbol);
    }
}
