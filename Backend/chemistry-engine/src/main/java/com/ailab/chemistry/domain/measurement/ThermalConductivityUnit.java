package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;

public enum ThermalConductivityUnit {
    WATT_PER_METER_KELVIN("W/(m·K)", BigDecimal.ONE);

    private final String symbol;
    private final BigDecimal factorToCanonical;

    ThermalConductivityUnit(String symbol, BigDecimal factorToCanonical) {
        this.symbol = symbol;
        this.factorToCanonical = factorToCanonical;
    }

    public String getSymbol() { return symbol; }
    public BigDecimal getFactorToCanonical() { return factorToCanonical; }
}
