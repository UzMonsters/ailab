package com.ailab.chemistry.domain.formula;

import java.math.BigInteger;
import java.util.Objects;

public final class ElementCount {
    private final ElementSymbol symbol;
    private final BigInteger count;

    public ElementCount(ElementSymbol symbol, BigInteger count) {
        this.symbol = Objects.requireNonNull(symbol, "Symbol must not be null");
        this.count = Objects.requireNonNull(count, "Count must not be null");
        if (count.compareTo(BigInteger.ZERO) <= 0) {
            throw new IllegalArgumentException("Count must be positive");
        }
    }

    public ElementSymbol getSymbol() {
        return symbol;
    }

    public BigInteger getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElementCount that = (ElementCount) o;
        return symbol.equals(that.symbol) && count.equals(that.count);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, count);
    }

    @Override
    public String toString() {
        return symbol.toString() + count;
    }
}
