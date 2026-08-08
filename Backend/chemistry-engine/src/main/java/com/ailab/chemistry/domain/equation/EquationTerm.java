package com.ailab.chemistry.domain.equation;

import java.math.BigInteger;
import java.util.Objects;
import com.ailab.chemistry.domain.formula.ChemicalFormula;

public final class EquationTerm {
    private final ChemicalFormula formula;
    private final BigInteger coefficient;
    private final EquationSide side;

    public EquationTerm(ChemicalFormula formula, BigInteger coefficient, EquationSide side) {
        this.formula = Objects.requireNonNull(formula, "Formula must not be null");
        this.coefficient = Objects.requireNonNull(coefficient, "Coefficient must not be null");
        this.side = Objects.requireNonNull(side, "Side must not be null");
        if (coefficient.compareTo(BigInteger.ZERO) <= 0) {
            throw new IllegalArgumentException("Coefficient must be strictly positive");
        }
    }

    public ChemicalFormula getFormula() {
        return formula;
    }

    public BigInteger getCoefficient() {
        return coefficient;
    }

    public EquationSide getSide() {
        return side;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquationTerm that = (EquationTerm) o;
        return coefficient.equals(that.coefficient) &&
                side == that.side &&
                formula.equals(that.formula);
    }

    @Override
    public int hashCode() {
        return Objects.hash(formula, coefficient, side);
    }

    @Override
    public String toString() {
        String coeffStr = coefficient.equals(BigInteger.ONE) ? "" : coefficient.toString();
        return coeffStr + formula.getNormalizedFormula();
    }
}
