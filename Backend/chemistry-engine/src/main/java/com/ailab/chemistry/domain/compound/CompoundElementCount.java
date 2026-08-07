package com.ailab.chemistry.domain.compound;

import java.math.BigInteger;
import java.util.Objects;

public final class CompoundElementCount implements Comparable<CompoundElementCount> {
    private final int atomicNumber;
    private final String symbol;
    private final BigInteger atomCount;

    public CompoundElementCount(int atomicNumber, String symbol, BigInteger atomCount) {
        if (atomicNumber < 1 || atomicNumber > 118) {
            throw new CompoundException(CompoundErrorCode.INVALID_COMPOUND_COMPOSITION, "Invalid atomic number: " + atomicNumber);
        }
        if (symbol == null || symbol.isBlank()) {
            throw new CompoundException(CompoundErrorCode.INVALID_COMPOUND_COMPOSITION, "Element symbol cannot be blank");
        }
        if (atomCount == null || atomCount.compareTo(BigInteger.ZERO) <= 0) {
            throw new CompoundException(CompoundErrorCode.INVALID_COMPOUND_COMPOSITION, "Atom count must be strictly positive (> 0)");
        }
        this.atomicNumber = atomicNumber;
        this.symbol = symbol.trim();
        this.atomCount = atomCount;
    }

    public int getAtomicNumber() {
        return atomicNumber;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigInteger getAtomCount() {
        return atomCount;
    }

    @Override
    public int compareTo(CompoundElementCount o) {
        return Integer.compare(this.atomicNumber, o.atomicNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompoundElementCount that = (CompoundElementCount) o;
        return atomicNumber == that.atomicNumber && Objects.equals(symbol, that.symbol) && Objects.equals(atomCount, that.atomCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(atomicNumber, symbol, atomCount);
    }

    @Override
    public String toString() {
        return symbol + "=" + atomCount;
    }
}
