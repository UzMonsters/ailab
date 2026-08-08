package com.ailab.chemistry.domain.electrochemistry;

import java.math.BigDecimal;

public enum ElectricChargeUnit {
    COULOMB("1");

    private final BigDecimal factorToCoulomb;

    ElectricChargeUnit(String factorToCoulomb) {
        this.factorToCoulomb = new BigDecimal(factorToCoulomb);
    }

    public BigDecimal factorToCoulomb() {
        return factorToCoulomb;
    }
}
