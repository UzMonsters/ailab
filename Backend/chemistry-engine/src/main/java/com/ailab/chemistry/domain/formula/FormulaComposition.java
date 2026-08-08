package com.ailab.chemistry.domain.formula;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public final class FormulaComposition {
    private final Map<ElementSymbol, BigInteger> composition;

    public FormulaComposition(Map<ElementSymbol, BigInteger> map) {
        Objects.requireNonNull(map, "Composition map must not be null");
        Map<ElementSymbol, BigInteger> sortedMap = new TreeMap<>(Comparator.comparing(ElementSymbol::getSymbol));
        for (Map.Entry<ElementSymbol, BigInteger> entry : map.entrySet()) {
            if (entry.getValue().compareTo(BigInteger.ZERO) > 0) {
                sortedMap.put(entry.getKey(), entry.getValue());
            }
        }
        this.composition = Collections.unmodifiableMap(sortedMap);
    }

    public Map<ElementSymbol, BigInteger> getComposition() {
        return composition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FormulaComposition that = (FormulaComposition) o;
        return composition.equals(that.composition);
    }

    @Override
    public int hashCode() {
        return composition.hashCode();
    }

    @Override
    public String toString() {
        return composition.toString();
    }
}
