package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;
import com.ailab.chemistry.domain.measurement.exception.IncompatibleUnitException;

public enum MolarConcentrationUnit {
    MOL_PER_LITER("mol/L", BigDecimal.ONE),
    MILLIMOL_PER_LITER("mmol/L", new BigDecimal("0.001"));

    private final String symbol;
    private final BigDecimal factor;

    MolarConcentrationUnit(String symbol, BigDecimal factor) {
        this.symbol = symbol;
        this.factor = factor;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getFactor() {
        return factor;
    }

    public static MolarConcentrationUnit fromSymbol(String symbol) {
        if (symbol == null) {
            throw new IllegalArgumentException("Symbol must not be null");
        }
        for (MolarConcentrationUnit unit : values()) {
            if (unit.symbol.equalsIgnoreCase(symbol)) {
                return unit;
            }
        }
        throw new IncompatibleUnitException("Unknown molar concentration unit symbol: " + symbol);
    }
}
