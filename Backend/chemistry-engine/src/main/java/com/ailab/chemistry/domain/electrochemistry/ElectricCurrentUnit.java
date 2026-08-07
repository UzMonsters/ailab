package com.ailab.chemistry.domain.electrochemistry;

import java.math.BigDecimal;

public enum ElectricCurrentUnit {
    AMPERE("1"),
    MILLIAMPERE("0.001");

    private final BigDecimal factorToAmpere;

    ElectricCurrentUnit(String factorToAmpere) {
        this.factorToAmpere = new BigDecimal(factorToAmpere);
    }

    public BigDecimal factorToAmpere() {
        return factorToAmpere;
    }
}
