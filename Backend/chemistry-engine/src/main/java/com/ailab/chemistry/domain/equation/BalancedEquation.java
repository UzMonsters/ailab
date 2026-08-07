package com.ailab.chemistry.domain.equation;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class BalancedEquation {
    private final String canonicalEquationString;
    private final List<EquationTerm> balancedReactants;
    private final List<EquationTerm> balancedProducts;
    private final boolean originallyBalanced;
    private final boolean atomBalanced;
    private final boolean chargeBalanced;

    public BalancedEquation(List<EquationTerm> balancedReactants, List<EquationTerm> balancedProducts, boolean originallyBalanced, boolean atomBalanced, boolean chargeBalanced) {
        this.balancedReactants = Collections.unmodifiableList(Objects.requireNonNull(balancedReactants, "balancedReactants must not be null"));
        this.balancedProducts = Collections.unmodifiableList(Objects.requireNonNull(balancedProducts, "balancedProducts must not be null"));
        this.originallyBalanced = originallyBalanced;
        this.atomBalanced = atomBalanced;
        this.chargeBalanced = chargeBalanced;

        String reactantsStr = balancedReactants.stream().map(EquationTerm::toString).collect(Collectors.joining(" + "));
        String productsStr = balancedProducts.stream().map(EquationTerm::toString).collect(Collectors.joining(" + "));
        this.canonicalEquationString = reactantsStr + " -> " + productsStr;
    }

    public String getCanonicalEquationString() {
        return canonicalEquationString;
    }

    public List<EquationTerm> getBalancedReactants() {
        return balancedReactants;
    }

    public List<EquationTerm> getBalancedProducts() {
        return balancedProducts;
    }

    public boolean isOriginallyBalanced() {
        return originallyBalanced;
    }

    public boolean isAtomBalanced() {
        return atomBalanced;
    }

    public boolean isChargeBalanced() {
        return chargeBalanced;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BalancedEquation that = (BalancedEquation) o;
        return originallyBalanced == that.originallyBalanced &&
                atomBalanced == that.atomBalanced &&
                chargeBalanced == that.chargeBalanced &&
                balancedReactants.equals(that.balancedReactants) &&
                balancedProducts.equals(that.balancedProducts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(balancedReactants, balancedProducts, originallyBalanced, atomBalanced, chargeBalanced);
    }

    @Override
    public String toString() {
        return canonicalEquationString;
    }
}
