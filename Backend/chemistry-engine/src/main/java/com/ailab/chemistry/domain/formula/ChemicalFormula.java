package com.ailab.chemistry.domain.formula;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public final class ChemicalFormula {
    private final String originalFormula;
    private final String normalizedFormula;
    private final Map<ElementSymbol, BigInteger> elementCounts;
    private final int netCharge;
    private final boolean electron;

    public ChemicalFormula(String originalFormula, String normalizedFormula, Map<ElementSymbol, BigInteger> elementCounts, int netCharge, boolean electron) {
        this.originalFormula = Objects.requireNonNull(originalFormula, "Original formula must not be null");
        this.normalizedFormula = Objects.requireNonNull(normalizedFormula, "Normalized formula must not be null");
        this.elementCounts = Collections.unmodifiableMap(new TreeMap<>(elementCounts));
        this.netCharge = netCharge;
        this.electron = electron;
    }

    public String getOriginalFormula() {
        return originalFormula;
    }

    public String getNormalizedFormula() {
        return normalizedFormula;
    }

    public Map<ElementSymbol, BigInteger> getElementCounts() {
        return elementCounts;
    }

    public int getNetCharge() {
        return netCharge;
    }

    public boolean isElectron() {
        return electron;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChemicalFormula that = (ChemicalFormula) o;
        return netCharge == that.netCharge &&
                electron == that.electron &&
                normalizedFormula.equals(that.normalizedFormula) &&
                elementCounts.equals(that.elementCounts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(normalizedFormula, elementCounts, netCharge, electron);
    }

    @Override
    public String toString() {
        return normalizedFormula;
    }
}
